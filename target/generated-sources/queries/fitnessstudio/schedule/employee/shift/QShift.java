package fitnessstudio.schedule.employee.shift;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QShift is a Querydsl query type for Shift
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QShift extends EntityPathBase<Shift> {

    private static final long serialVersionUID = -115815555L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QShift shift = new QShift("shift");

    public final fitnessstudio.schedule.entry.QScheduleEntry _super = new fitnessstudio.schedule.entry.QScheduleEntry(this);

    public final EnumPath<fitnessstudio.schedule.employee.shift.department.Department> department = createEnum("department", fitnessstudio.schedule.employee.shift.department.Department.class);

    public final fitnessstudio.user.employee.QEmployee employee;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> end = _super.end;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> start = _super.start;

    public QShift(String variable) {
        this(Shift.class, forVariable(variable), INITS);
    }

    public QShift(Path<? extends Shift> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QShift(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QShift(PathMetadata metadata, PathInits inits) {
        this(Shift.class, metadata, inits);
    }

    public QShift(Class<? extends Shift> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.employee = inits.isInitialized("employee") ? new fitnessstudio.user.employee.QEmployee(forProperty("employee"), inits.get("employee")) : null;
    }

}

