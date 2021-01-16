package fitnessstudio.user.customer;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCustomer is a Querydsl query type for Customer
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QCustomer extends EntityPathBase<Customer> {

    private static final long serialVersionUID = -1893281061L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCustomer customer = new QCustomer("customer");

    public final fitnessstudio.user.QUser _super;

    // inherited
    public final fitnessstudio.user.QUserAddress address;

    public final ComparablePath<org.javamoney.moneta.Money> balance = createComparable("balance", org.javamoney.moneta.Money.class);

    public final fitnessstudio.contracts.QContract contract;

    //inherited
    public final NumberPath<Long> id;

    // inherited
    public final org.salespointframework.useraccount.QUserAccount userAccount;

    public QCustomer(String variable) {
        this(Customer.class, forVariable(variable), INITS);
    }

    public QCustomer(Path<? extends Customer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCustomer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCustomer(PathMetadata metadata, PathInits inits) {
        this(Customer.class, metadata, inits);
    }

    public QCustomer(Class<? extends Customer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new fitnessstudio.user.QUser(type, metadata, inits);
        this.address = _super.address;
        this.contract = inits.isInitialized("contract") ? new fitnessstudio.contracts.QContract(forProperty("contract")) : null;
        this.id = _super.id;
        this.userAccount = _super.userAccount;
    }

}

