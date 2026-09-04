package fr.diginamic.demospring.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import fr.diginamic.demospring.dto.DepartmentDto;
import fr.diginamic.demospring.exception.FunctionalException;
import fr.diginamic.demospring.exception.NotFoundException;
import fr.diginamic.demospring.model.City;
import fr.diginamic.demospring.model.Department;
import fr.diginamic.demospring.repository.CityRepository;
import fr.diginamic.demospring.repository.DepartmentRepository;
import fr.diginamic.demospring.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * REST endpoints for departments, exposed under {@code /departments}.
 *
 * <p>All payloads are {@link DepartmentDto}. Errors are translated into
 * {@link fr.diginamic.demospring.exception.ApiError} responses by
 * {@link fr.diginamic.demospring.exception.GlobalExceptionHandler}.</p>
 */
@RestController
@RequestMapping("/departments")
@Validated
@Tag(name = "Departments", description = "Read and manage departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final CityRepository cityRepository;
    private final DepartmentRepository departmentRepository;

    /** French flag blue - primary accent (title, table header, flag). */
    private static final BaseColor FRENCH_BLUE = new BaseColor(0x00, 0x55, 0xA4);
    /** French flag red - used sparingly (flag + a small accent under the title). */
    private static final BaseColor FRENCH_RED = new BaseColor(0xEF, 0x41, 0x35);
    /** Light gray used for the thin rule under each table row. */
    private static final BaseColor ROW_LINE_GRAY = new BaseColor(0xDD, 0xDD, 0xDD);
    /** Gray used for secondary text (region line, footer, stat labels). */
    private static final BaseColor TEXT_GRAY = new BaseColor(0x66, 0x66, 0x66);
    /** Light tint used behind each stat card. */
    private static final BaseColor CARD_BG = new BaseColor(0xF5, 0xF7, 0xFB);

    /**
     * @param departmentService   department business service
     * @param cityRepository      city repository, used by the PDF export
     * @param departmentRepository department repository, used by the PDF export
     */
    public DepartmentController(DepartmentService departmentService,  CityRepository cityRepository, DepartmentRepository departmentRepository) {
        this.departmentService = departmentService;
        this.cityRepository = cityRepository;
        this.departmentRepository = departmentRepository;
    }

    /**
     * @return every department
     */
    @GetMapping
    @Operation(summary = "List all departments")
    public List<DepartmentDto> getDepartments() {
        return departmentService.getDepartments();
    }

    /**
     * @param id department id
     * @return the matching department
     * @throws NotFoundException if no department has this id
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a department by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department found"),
            @ApiResponse(responseCode = "404", description = "No department with this id")
    })
    public DepartmentDto getDepartmentById(@Parameter(description = "Department id") @PathVariable
                                           @Positive(message = "Department id must be a positive number.") int id) throws NotFoundException {
        return departmentService.getDepartmentById(id)
                .orElseThrow(() -> new NotFoundException("Department with id " + id + " not found"));
    }

    /**
     * Exports a department as a PDF file: the department name as the title,
     * followed by the department code, the department name and the list of its
     * cities (name and population), most populated first.
     *
     * @param code     INSEE department code
     * @param response servlet response the PDF is streamed to as an attachment
     * @throws IOException         if the response cannot be written
     * @throws DocumentException   if the PDF cannot be generated
     * @throws FunctionalException if no department has this code
     */
    @GetMapping("/{code}/export/pdf")
    @Operation(summary = "Export a department and its cities as a PDF file")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF file (application/pdf) streamed as an attachment"),
            @ApiResponse(responseCode = "400", description = "No department with this code")
    })
    public void exportDepartmentAsPdf(@Parameter(description = "INSEE department code") @PathVariable String code,
                                      HttpServletResponse response)
            throws IOException, DocumentException, FunctionalException {

        Department departement = departmentRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new FunctionalException("Department '" + code + "' not found"));

        List<City> villes = cityRepository.findByDepartmentCodeIgnoreCaseOrderByPopulationDesc(code);

        long totalPopulation = 0;
        for (City ville : villes) {
            totalPopulation += ville.getPopulation();
        }
        String mostPopulatedCity = villes.isEmpty() ? "-" : villes.get(0).getName();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"department_" + code + ".pdf\"");

        Document document = new Document(PageSize.A4, 44, 44, 48, 54);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new FooterPageEvent(departement.getCode()));
        document.open();
        document.addTitle(departement.getName());
        document.addCreator("Diginamic Demo Spring");

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, FRENCH_BLUE);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11, TEXT_GRAY);
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

        Paragraph title = new Paragraph(departement.getName(), titleFont);
        title.setSpacingAfter(3f);
        document.add(title);

        // Region name (if loaded) reads under the department name, e.g. "Auvergne-Rhone-Alpes - Department code 69".
        String regionName = departement.getRegion() != null ? departement.getRegion().getName() : null;
        String subtitleText = (regionName != null ? regionName + " \u00b7 " : "") + "Department code " + departement.getCode();
        Paragraph subtitle = new Paragraph(subtitleText, subtitleFont);
        subtitle.setSpacingAfter(8f);
        document.add(subtitle);

        NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.FRANCE);
        document.add(buildStatsRow(villes.size(), totalPopulation, mostPopulatedCity, numberFormat));

        if (villes.isEmpty()) {
            document.add(new Paragraph("No cities found for this department.", tableCellFont));
        } else {
            PdfPTable table = new PdfPTable(new float[]{1f, 4f, 2.5f, 2f});
            table.setWidthPercentage(100);
            table.setSpacingAfter(14f);
            table.setHeaderRows(1);
            table.setTableEvent(new RoundedHeaderEvent());

            addHeaderCell(table, "#", tableHeaderFont, Element.ALIGN_CENTER);
            addHeaderCell(table, "City", tableHeaderFont, Element.ALIGN_LEFT);
            addHeaderCell(table, "Population", tableHeaderFont, Element.ALIGN_CENTER);
            addHeaderCell(table, "Share", tableHeaderFont, Element.ALIGN_CENTER);

            for (int i = 0; i < villes.size(); i++) {
                City ville = villes.get(i);
                boolean lastRow = (i == villes.size() - 1);
                double share = totalPopulation == 0 ? 0 : (100.0 * ville.getPopulation() / totalPopulation);

                addBodyCell(table, String.valueOf(i + 1), tableCellFont, Element.ALIGN_CENTER, lastRow);
                addBodyCell(table, ville.getName(), tableCellFont, Element.ALIGN_LEFT, lastRow);
                addBodyCell(table, numberFormat.format(ville.getPopulation()), tableCellFont, Element.ALIGN_CENTER, lastRow);
                addBodyCell(table, String.format(Locale.FRANCE, "%.1f%%", share), tableCellFont, Element.ALIGN_CENTER, lastRow);
            }

            document.add(table);
        }

        document.close();
    }

    /**
     * Draws the main table's header row as a single rounded-rectangle band (instead
     * of four square-cornered cells), so the table reads as a softly rounded container.
     */
    private static final class RoundedHeaderEvent implements PdfPTableEvent {
        private static final float RADIUS = 4f;

        @Override
        public void tableLayout(PdfPTable table, float[][] widths, float[] heights, int headerRows,
                                int rowStart, PdfContentByte[] canvases) {
            if (widths.length == 0 || heights.length < 2) {
                return;
            }
            float[] headerRowWidths = widths[0];
            float xLeft = headerRowWidths[0];
            float xRight = headerRowWidths[headerRowWidths.length - 1];
            float yTop = heights[0];
            float yBottom = heights[1];

            PdfContentByte cb = canvases[PdfPTable.BACKGROUNDCANVAS];
            cb.saveState();
            cb.setColorFill(FRENCH_BLUE);
            cb.roundRectangle(xLeft, yBottom, xRight - xLeft, yTop - yBottom, RADIUS);
            cb.fill();
            cb.restoreState();
        }
    }

    /**
     * Draws a stat card's background as a fully rounded rectangle: a light tint fill,
     * a tricolor (blue/white/red) stripe along the top edge clipped to the rounding,
     * and a thin gray border outline.
     */
    private static final class StatCardBackgroundEvent implements PdfPCellEvent {
        private static final float RADIUS = 5f;
        private static final float STRIPE_HEIGHT = 2.5f;

        @Override
        public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
            float x = position.getLeft();
            float y = position.getBottom();
            float w = position.getWidth();
            float h = position.getHeight();
            float bandWidth = w / 3f;

            PdfContentByte background = canvases[PdfPTable.BACKGROUNDCANVAS];
            background.saveState();
            background.roundRectangle(x, y, w, h, RADIUS);
            background.clip();
            background.newPath();

            background.setColorFill(CARD_BG);
            background.rectangle(x, y, w, h);
            background.fill();

            background.setColorFill(FRENCH_BLUE);
            background.rectangle(x, y + h - STRIPE_HEIGHT, bandWidth, STRIPE_HEIGHT);
            background.fill();

            background.setColorFill(BaseColor.WHITE);
            background.rectangle(x + bandWidth, y + h - STRIPE_HEIGHT, bandWidth, STRIPE_HEIGHT);
            background.fill();

            background.setColorFill(FRENCH_RED);
            background.rectangle(x + 2 * bandWidth, y + h - STRIPE_HEIGHT, w - 2 * bandWidth, STRIPE_HEIGHT);
            background.fill();
            background.restoreState();

            PdfContentByte line = canvases[PdfPTable.LINECANVAS];
            line.saveState();
            line.setColorStroke(ROW_LINE_GRAY);
            line.setLineWidth(0.75f);
            line.roundRectangle(x, y, w, h, RADIUS);
            line.stroke();
            line.restoreState();
        }
    }

    /**
     * Builds the row of three summary cards shown above the table: number of
     * cities, total population, and the most populated city. Cards are separated
     * by thin transparent spacer columns so they read as distinct blocks.
     */
    private static PdfPTable buildStatsRow(int cityCount, long totalPopulation, String mostPopulatedCity,
                                           NumberFormat numberFormat) {
        PdfPTable row = new PdfPTable(new float[]{1f, 0.08f, 1f, 0.08f, 1f});
        row.setWidthPercentage(100);
        row.setSpacingAfter(16f);

        row.addCell(buildStatCard("Cities", String.valueOf(cityCount)));
        row.addCell(spacerCell());
        row.addCell(buildStatCard("Total population", numberFormat.format(totalPopulation) + " inh."));
        row.addCell(spacerCell());
        row.addCell(buildStatCard("Most populated city", mostPopulatedCity));

        return row;
    }

    /**
     * A single summary card: rounded corners, a tricolor top stripe (via
     * {@link StatCardBackgroundEvent}), a small gray label and a bold value.
     */
    private static PdfPCell buildStatCard(String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, TEXT_GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setCellEvent(new StatCardBackgroundEvent());
        cell.setPaddingLeft(10f);
        cell.setPaddingRight(10f);
        cell.setPaddingTop(13f);
        cell.setPaddingBottom(10f);

        Paragraph labelParagraph = new Paragraph(label.toUpperCase(Locale.ROOT), labelFont);
        labelParagraph.setSpacingAfter(4f);
        cell.addElement(labelParagraph);
        cell.addElement(new Paragraph(value, valueFont));

        return cell;
    }

    /** A borderless, unfilled cell used to put visual space between two stat cards. */
    private static PdfPCell spacerCell() {
        PdfPCell spacer = new PdfPCell();
        spacer.setBorder(PdfPCell.NO_BORDER);
        return spacer;
    }

    /**
     * Adds a table header cell: text only - the blue rounded band behind it is
     * drawn once for the whole row by {@link RoundedHeaderEvent}.
     */
    private static void addHeaderCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6f);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
    }

    /**
     * Adds a data cell to the table: no fill, just a thin gray rule under the row
     * (omitted on the last row) so the table stays minimal rather than boxed or striped.
     */
    private static void addBodyCell(PdfPTable table, String text, Font font, int alignment, boolean lastRow) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        cell.setBorder(lastRow ? PdfPCell.NO_BORDER : PdfPCell.BOTTOM);
        cell.setBorderColor(ROW_LINE_GRAY);
        cell.setBorderWidth(0.6f);
        table.addCell(cell);
    }

    /**
     * Draws the footer on every page: department code, generation timestamp and
     * page number - centered, small gray text.
     */
    private static final class FooterPageEvent extends PdfPageEventHelper {

        private final String departmentCode;
        private final Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_GRAY);

        private FooterPageEvent(String departmentCode) {
            this.departmentCode = departmentCode;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase footer = new Phrase(
                    "Department " + departmentCode + " - generated "
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            + " - page " + writer.getPageNumber(),
                    footerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer,
                    (document.right() + document.left()) / 2, document.bottom() - 20, 0);
        }
    }

    /**
     * Creates a department.
     *
     * @param department the department to create
     * @return the created department
     * @throws FunctionalException if a department with the same code already exists
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a department")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Department created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or duplicate code")
    })
    public DepartmentDto addDepartment(@Valid @RequestBody DepartmentDto department) throws FunctionalException {
        return departmentService.addDepartment(department);
    }

    /**
     * Updates a department.
     *
     * @param id         id of the department to update
     * @param department new values
     * @return the updated department
     * @throws NotFoundException if no department has this id
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "No department with this id")
    })
    public DepartmentDto updateDepartment(@Parameter(description = "Department id") @PathVariable
                                          @Positive(message = "Department id must be a positive number.") int id,
                                          @Valid @RequestBody DepartmentDto department)
            throws NotFoundException {
        return departmentService.updateDepartment(id, department);
    }

    /**
     * Deletes a department.
     *
     * @param id id of the department to delete
     * @throws NotFoundException if no department has this id
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a department")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Department deleted"),
            @ApiResponse(responseCode = "404", description = "No department with this id")
    })
    public void deleteDepartment(@Parameter(description = "Department id") @PathVariable
                                 @Positive(message = "Department id must be a positive number.") int id) throws NotFoundException {
        departmentService.deleteDepartment(id);
    }
}