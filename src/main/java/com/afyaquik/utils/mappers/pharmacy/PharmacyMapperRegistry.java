package com.afyaquik.utils.mappers.pharmacy;

import com.afyaquik.utils.mappers.MapperRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PharmacyMapperRegistry {

    @Autowired
    public PharmacyMapperRegistry(DrugInventoryMapper drugInventoryMapper, DrugCategoryMapper drugCategoryMapper,
                                 DrugMapper drugMapper, DrugFormMapper drugFormMapper,
                                 PatientDrugMapper patientDrugMapper, MapperRegistry mapperRegistry) {
        mapperRegistry.registerMapper("drugInventory", drugInventoryMapper);
        mapperRegistry.registerMapper("drugCategories", drugCategoryMapper);
        mapperRegistry.registerMapper("drugs", drugMapper);
        mapperRegistry.registerMapper("drugForms", drugFormMapper);
        mapperRegistry.registerMapper("patientDrugs", patientDrugMapper);

    }

}
