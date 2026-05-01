package ru.equalizationofgeodeticnetworks.persistence.dto;

import ru.equalizationofgeodeticnetworks.validation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
@UniquePointNames
@ConsistentNetworkType
public class AdjustmentRequest implements PointNamesContainer {
    @NotNull
    private NetworkType networkType;
    @NotNull
    private AdjustmentMethodType method;
    @Valid
    private InstrumentErrors instrumentErrors;
    @NotEmpty
    private List<@Valid FixedPointDto> fixedPoints;
    private List<@Valid UnknownPointDto> unknownPoints;
    @NotEmpty
    @ValidTraverseOrder
    private List<@Valid MeasurementDto> measurements;
    private List<OutputField> requestedOutput;
    @Valid
    private StartEndDirections startDirections;
    @Valid
    private StartEndDirections endDirections;

    public enum NetworkType { POLYGONOMETRY, LEVELING }
    public enum AdjustmentMethodType { RECURRENT, PARAMETRIC, L1, M_ESTIMATE }
    public enum OutputField { COORDINATES, COORDINATE_ERRORS, COVAR_MATRIX, MEASUREMENTS_RESIDUALS, UNIT_WEIGHT_ERROR, RELIABILITY }
}
