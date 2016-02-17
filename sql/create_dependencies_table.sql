-- Table: attribute_dependencies

-- DROP TABLE attribute_dependencies;

CREATE TABLE attribute_dependencies
(
  dataset_name character varying(16) NOT NULL,
  num_features integer,
  features character varying(256) NOT NULL,
  target character varying(32) NOT NULL,
  pa_min numeric,
  pa_max numeric,
  pa_distance numeric,
  CONSTRAINT primary_key PRIMARY KEY (dataset_name, features, target)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE attribute_dependencies
  OWNER TO postgres;