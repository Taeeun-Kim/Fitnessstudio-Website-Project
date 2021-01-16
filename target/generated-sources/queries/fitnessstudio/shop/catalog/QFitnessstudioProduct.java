package fitnessstudio.shop.catalog;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFitnessstudioProduct is a Querydsl query type for FitnessstudioProduct
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFitnessstudioProduct extends EntityPathBase<FitnessstudioProduct> {

    private static final long serialVersionUID = 324234222L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFitnessstudioProduct fitnessstudioProduct = new QFitnessstudioProduct("fitnessstudioProduct");

    public final org.salespointframework.catalog.QProduct _super;

    public final BooleanPath canExpire = createBoolean("canExpire");

    //inherited
    public final SetPath<String, StringPath> categories;

    public final StringPath description = createString("description");

    public final ComparablePath<org.javamoney.moneta.Money> discount = createComparable("discount", org.javamoney.moneta.Money.class);

    public final NumberPath<Integer> discountPercentage = createNumber("discountPercentage", Integer.class);

    public final StringPath image = createString("image");

    //inherited
    public final EnumPath<org.salespointframework.quantity.Metric> metric;

    //inherited
    public final StringPath name;

    public final SimplePath<javax.money.MonetaryAmount> price = createSimple("price", javax.money.MonetaryAmount.class);

    // inherited
    public final org.salespointframework.catalog.QProductIdentifier productIdentifier;

    public final SimplePath<javax.money.MonetaryAmount> uvp = createSimple("uvp", javax.money.MonetaryAmount.class);

    public QFitnessstudioProduct(String variable) {
        this(FitnessstudioProduct.class, forVariable(variable), INITS);
    }

    public QFitnessstudioProduct(Path<? extends FitnessstudioProduct> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFitnessstudioProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFitnessstudioProduct(PathMetadata metadata, PathInits inits) {
        this(FitnessstudioProduct.class, metadata, inits);
    }

    public QFitnessstudioProduct(Class<? extends FitnessstudioProduct> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.catalog.QProduct(type, metadata, inits);
        this.categories = _super.categories;
        this.metric = _super.metric;
        this.name = _super.name;
        this.productIdentifier = _super.productIdentifier;
    }

}

