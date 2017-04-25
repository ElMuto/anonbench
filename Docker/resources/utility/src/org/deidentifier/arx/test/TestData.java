/*
 * ARX: Powerful Data Anonymization
 * Copyright 2012 - 2015 Florian Kohlmayer, Fabian Prasser
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deidentifier.arx.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.Data;

public class TestData {
    
    public static enum Dataset {
        ADULT {
            @Override
            public String toString() {
                return "ADULT";
            }
        },
        CUP {
            @Override
            public String toString() {
                return "CUP";
            }
        },
        FARS {
            @Override
            public String toString() {
                return "FARS";
            }
        },
        ATUS {
            @Override
            public String toString() {
                return "ATUS";
            }
        },
        IHIS {
            @Override
            public String toString() {
                return "IHIS";
            }
        },
        SS13ACS {
            @Override
            public String toString() {
                return "SS13ACS";
            }
        },
        TEST {
            @Override
            public String toString() {
                return "Test";
            }
        };
    }
    
    public static enum UtilityMeasure {
        AECS {
            @Override
            public String toString() {
                return "AECS";
            }
        },
        AMBIGUITY_METRIC {
            @Override
            public String toString() {
                return "Ambiguity";
            }
        },
        DISCERNABILITY {
            @Override
            public String toString() {
                return "Discernability";
            }
        },
        ENTROPY {
            @Override
            public String toString() {
                return "Entropy";
            }
        },
        GENERIC_ENTROPY {
            @Override
            public String toString() {
                return "GenericEntropy";
            }
        },
        ENTROPY_NORMALIZED {
            @Override
            public String toString() {
                return "EntropyNormalized";
            }
        },
        KL_DIVERGENCE {
            @Override
            public String toString() {
                return "KLDivergence";
            }
        },
        LOSS {
            @Override
            public String toString() {
                return "Loss";
            }
        },
        PRECISION {
            @Override
            public String toString() {
                return "Precision";
            }
        },
    }
    
    /**
     * Configures and returns the dataset
     * @param dataset
     * @return
     * @throws IOException
     */
    public static Data getData(Dataset dataset) throws IOException {
        return getData(dataset, Integer.MAX_VALUE);
    }
    
    /**
     * Configures and returns the dataset
     * @param dataset
     * @param criterion
     * @param qiCount
     * @return
     * @throws IOException
     */
    public static Data getData(Dataset dataset,
                               int qiCount) throws IOException {
        Data data = null;
        switch (dataset) {
        case ADULT:
            data = Data.create("data/adult.csv", StandardCharsets.UTF_8, ';');
            break;
        case TEST:
            data = Data.create("data/test.csv", StandardCharsets.UTF_8, ';');
            break;
        case ATUS:
            data = Data.create("data/atus.csv", StandardCharsets.UTF_8, ';');
            break;
        case CUP:
            data = Data.create("data/cup.csv", StandardCharsets.UTF_8, ';');
            break;
        case FARS:
            data = Data.create("data/fars.csv", StandardCharsets.UTF_8, ';');
            break;
        case IHIS:
            data = Data.create("data/ihis.csv", StandardCharsets.UTF_8, ';');
            break;
        case SS13ACS:
            data = Data.create("data/ss13acs.csv", StandardCharsets.UTF_8, ';');
            break;
        default:
            throw new RuntimeException("Invalid dataset");
        }
        
        int count = 0;
        for (String qi : getQuasiIdentifyingAttributes(dataset)) {
            data.getDefinition().setAttributeType(qi, getHierarchy(dataset, qi));
            count++;
            if (count > qiCount) {
                break;
            }
        }
        
        return data;
    }
    
    /**
     * Returns the generalization hierarchy for the dataset and attribute
     * @param dataset
     * @param attribute
     * @return
     * @throws IOException
     */
    public static Hierarchy getHierarchy(Dataset dataset, String attribute) throws IOException {
        switch (dataset) {
        case ADULT:
            return Hierarchy.create("hierarchies/adult_hierarchy_" + attribute + ".csv", StandardCharsets.UTF_8, ';');
        case TEST:
            return Hierarchy.create("hierarchies/test_hierarchy_" + attribute + ".csv", StandardCharsets.UTF_8, ';');
        case ATUS:
            return Hierarchy.create("hierarchies/atus_hierarchy_" + attribute + ".csv", StandardCharsets.UTF_8, ';');
        case CUP:
            return Hierarchy.create("hierarchies/cup_hierarchy_" + attribute + ".csv", StandardCharsets.UTF_8, ';');
        case FARS:
            return Hierarchy.create("hierarchies/fars_hierarchy_" + attribute + ".csv", StandardCharsets.UTF_8, ';');
        case IHIS:
            return Hierarchy.create("hierarchies/ihis_hierarchy_" + attribute + ".csv", StandardCharsets.UTF_8, ';');
        case SS13ACS:
            return Hierarchy.create("hierarchies/ss13acs_hierarchy_" + attribute + ".csv", StandardCharsets.UTF_8, ';');
        default:
            throw new RuntimeException("Invalid dataset");
        }
    }
    
    /**
     * Returns the quasi-identifiers for the dataset
     * @param dataset
     * @return
     */
    public static String[] getQuasiIdentifyingAttributes(Dataset dataset) {
        switch (dataset) {
        case ADULT:
            return new String[] {
                    "age",
                    "education",
                    "marital-status",
                    "native-country",
                    "occupation",
                    "race",
                    "salary-class",
                    "sex",
                    "workclass",
            };
        case TEST:
            return new String[] {
                    "age",
                    "gender",
                    "zipcode",
            };
        case ATUS:
            return new String[] {
                    "Age",
                    "Birthplace",
                    "Citizenship status",
                    "Highest level of school completed",
                    "Labor force status",
                    "Marital status",
                    "Race",
                    "Region",
                    "Sex",
            };
        case CUP:
            return new String[] {
                    "AGE",
                    "GENDER",
                    "INCOME",
                    "MINRAMNT",
                    "NGIFTALL",
                    "RAMNTALL",
                    "STATE",
                    "ZIP",
            };
        case FARS:
            return new String[] {
                    "iage",
                    "ideathday",
                    "ideathmon",
                    "ihispanic",
                    "iinjury",
                    "irace",
                    "isex",
                    "istatenum",
            };
        case IHIS:
            return new String[] {
                    "AGE",
                    "EDUC",
                    "MARSTAT",
                    "PERNUM",
                    "QUARTER",
                    "RACEA",
                    "REGION",
                    "SEX",
                    "YEAR",
            };
        case SS13ACS:
            return new String[] {
                    "Age",
                    "Sex",
                    "Weight",
                    "Marital status",
                    "Education",
                    "Citizenship",
                    "Workclass",
                    "Relationship"
            };
            
        default:
            throw new RuntimeException("Unknown dataset: " + dataset.toString());
        }
    }
}
