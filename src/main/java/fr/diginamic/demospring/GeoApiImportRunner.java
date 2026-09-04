package fr.diginamic.demospring;

import fr.diginamic.demospring.dto.geo.DepartmentApiDto;
import fr.diginamic.demospring.dto.geo.RegionApiDto;
import fr.diginamic.demospring.model.Region;
import fr.diginamic.demospring.repository.DepartmentRepository;
import fr.diginamic.demospring.repository.RegionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Profile("import")
public class GeoApiImportRunner implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final RegionRepository regionRepository;

    public GeoApiImportRunner(DepartmentRepository departmentRepository,  RegionRepository regionRepository) {
        this.departmentRepository = departmentRepository;
        this.regionRepository = regionRepository;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(GeoApiImportRunner.class)
                .profiles("dev", "import")
                .web(WebApplicationType.NONE)
                .run(args);
        context.close();
    }

    @Override
    public void run(String... args) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        RegionApiDto[] regions = restTemplate.getForObject(
                "https://geo.api.gouv.fr/regions", RegionApiDto[].class);

        if (regions != null) {
            for (RegionApiDto apiRegion : regions) {
                Region region = regionRepository.findByCodeIgnoreCase(apiRegion.code())
                        .orElseGet(() -> new Region(apiRegion.code(), apiRegion.name()));
                region.setName(apiRegion.name());
                regionRepository.save(region);
            }
        }

        DepartmentApiDto[] departments = restTemplate.getForObject(
                "https://geo.api.gouv.fr/departements", DepartmentApiDto[].class);

        if (departments != null) {
            for (DepartmentApiDto apiDepartment : departments) {
                departmentRepository.findByCodeIgnoreCase(apiDepartment.code())
                        .ifPresent(department -> {
                            department.setName(apiDepartment.name());

                            if (apiDepartment.regionCode() != null) {
                                regionRepository.findByCodeIgnoreCase(apiDepartment.regionCode())
                                        .ifPresent(department::setRegion);
                            }

                            departmentRepository.save(department);
                        });
            }
        }

        System.out.println("Departments and regions updated from geo.api.gouv.fr.");
    }
}
