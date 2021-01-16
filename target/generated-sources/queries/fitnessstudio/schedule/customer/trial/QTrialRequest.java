package fitnessstudio.schedule.customer.trial;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrialRequest is a Querydsl query type for TrialRequest
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTrialRequest extends EntityPathBase<TrialRequest> {

    private static final long serialVersionUID = 1424915962L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTrialRequest trialRequest = new QTrialRequest("trialRequest");

    public final fitnessstudio.schedule.entry.QScheduleEntry _super = new fitnessstudio.schedule.entry.QScheduleEntry(this);

    public final fitnessstudio.user.customer.QCustomer customer;

    public final fitnessstudio.user.employee.QEmployee employee;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> end = _super.end;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> start = _super.start;

    public final EnumPath<fitnessstudio.schedule.request.RequestStatus> status = createEnum("status", fitnessstudio.schedule.request.RequestStatus.class);

    public QTrialRequest(String variable) {
        this(TrialRequest.class, forVariable(variable), INITS);
    }

    public QTrialRequest(Path<? extends TrialRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTrialRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTrialRequest(PathMetadata metadata, PathInits inits) {
        this(TrialRequest.class, metadata, inits);
    }

    public QTrialRequest(Class<? extends TrialRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.customer = inits.isInitialized("customer") ? new fitnessstudio.user.customer.QCustomer(forProperty("customer"), inits.get("customer")) : null;
        this.employee = inits.isInitialized("employee") ? new fitnessstudio.user.employee.QEmployee(forProperty("employee"), inits.get("employee")) : null;
    }

}

