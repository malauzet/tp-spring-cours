package fr.diginamic.demospring.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
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
import java.util.List;

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

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"department_" + code + ".pdf\"");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        document.addTitle(String.valueOf(departement.getName()));
        document.add(new Paragraph(String.valueOf(departement.getName())));
        document.add(new Paragraph("Department code: " + departement.getCode()));
        document.add(new Paragraph("Department name: " + departement.getName()));
        document.add(new Paragraph(" "));

        for (City ville : villes) {
            document.add(new Paragraph(ville.getName() + " - " + ville.getPopulation() + " inhabitants"));
        }

        document.close();
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
