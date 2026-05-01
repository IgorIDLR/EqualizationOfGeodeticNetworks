CREATE TABLE project (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         description TEXT,
                         created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                         updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE point (
                       id BIGSERIAL PRIMARY KEY,
                       project_id BIGINT NOT NULL REFERENCES project(id) ON DELETE CASCADE,
                       name VARCHAR(255) NOT NULL,
                       type VARCHAR(10) NOT NULL,
                       x DOUBLE PRECISION,
                       y DOUBLE PRECISION,
                       height DOUBLE PRECISION,
                       is_fixed BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE measurement (
                             id BIGSERIAL PRIMARY KEY,
                             project_id BIGINT NOT NULL REFERENCES project(id) ON DELETE CASCADE,
                             type VARCHAR(20) NOT NULL,
                             data JSONB NOT NULL,
                             sigma DOUBLE PRECISION NOT NULL,
                             weight DOUBLE PRECISION NOT NULL
);

CREATE TABLE adjustment_result (
                                   id BIGSERIAL PRIMARY KEY,
                                   project_id BIGINT NOT NULL REFERENCES project(id) ON DELETE CASCADE,
                                   coordinates JSONB NOT NULL,
                                   covariance JSONB NOT NULL,
                                   s0 DOUBLE PRECISION NOT NULL,
                                   created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_point_project ON point(project_id);
CREATE INDEX idx_measurement_project ON measurement(project_id);