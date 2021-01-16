package fitnessstudio.user.employee;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEmployee is a Querydsl query type for Employee
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QEmployee extends EntityPathBase<Employee> {

    private static final long serialVersionUID = 520453339L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEmployee employee = new QEmployee("employee");

    public final fitnessstudio.user.QUser _super;

    // inherited
    public final fitnessstudio.user.QUserAddress address;

    //inherited
    public final NumberPath<Long> id;

    public final ComparablePath<org.javamoney.moneta.Money> salary = createComparable("salary", org.javamoney.moneta.Money.class);

    // inherited
    public final org.salespointframework.useraccount.QUserAccount userAccount;

    public QEmployee(String variable) {
        this(Employee.class, forVariable(variable), INITS);
    }

    public QEmployee(Path<? extends Employee> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEmployee(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEmployee(PathMetadata metadata, PathInits inits) {
        this(Employee.class, metadata, inits);
    }

    public QEmployee(Class<? extends Employee> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new fitnessstudio.user.QUser(type, metadata, inits);
        this.address = _super.address;
        this.id = _super.id;
        this.userAccount = _super.userAccount;
    }

}

