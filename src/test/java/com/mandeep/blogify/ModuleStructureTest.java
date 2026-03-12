package com.mandeep.blogify;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModuleStructureTest {

    ApplicationModules modules = ApplicationModules.of(BlogifyApplication.class);

    @Test
    void verifyModuleStructure() {
        modules.verify();
    }

    @Test
    void printModuleStructure() {
        modules.forEach(System.out::println);
    }
}
