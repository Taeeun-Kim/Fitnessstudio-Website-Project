package fitnessstudio.schedule.customer.training;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTraining is a Querydsl query type for Training
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTraining extends EntityPathBase<Training> {

    private static final long serialVersionUID = 407189253L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTraining training = new QTraining("training");

    public final fitnessstudio.schedule.entry.QScheduleEntry _super = new fitnessstudio.schedule.entry.QScheduleEntry(this);

    public final fitnessstudio.user.customer.QCustomer customer;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> end = _super.end;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> start = _super.start;

    public QTraining(String variable) {
        this(Training.class, forVariable(variable), INITS);
    }

    public QTraining(Path<? extends Training> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTraining(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTraining(PathMetadata metadata, PathInits inits) {
        this(Training.class, metadata, inits);
    }

    public QTraining(Class<? extends Training> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.customer = inits.isInitialized("customer") ? new fitnessstudio.user.customer.QCustomer(forProperty("customer"), inits.get("customer")) : null;
    }

}

