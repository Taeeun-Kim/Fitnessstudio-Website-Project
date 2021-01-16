package fitnessstudio.shop.inventory;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFitnessstudioInventoryItem is a Querydsl query type for FitnessstudioInventoryItem
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFitnessstudioInventoryItem extends EntityPathBase<FitnessstudioInventoryItem> {

    private static final long serialVersionUID = -537108181L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFitnessstudioInventoryItem fitnessstudioInventoryItem = new QFitnessstudioInventoryItem("fitnessstudioInventoryItem");

    public final org.salespointframework.inventory.QMultiInventoryItem _super;

    public final SimplePath<java.util.Optional<java.time.LocalDate>> expiryDate = createSimple("expiryDate", java.util.Optional.class);

    // inherited
    public final org.salespointframework.inventory.QInventoryItemIdentifier inventoryItemIdentifier;

    public final org.salespointframework.catalog.QProduct product;

    // inherited
    public final org.salespointframework.quantity.QQuantity quantity;

    public QFitnessstudioInventoryItem(String variable) {
        this(FitnessstudioInventoryItem.class, forVariable(variable), INITS);
    }

    public QFitnessstudioInventoryItem(Path<? extends FitnessstudioInventoryItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFitnessstudioInventoryItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFitnessstudioInventoryItem(PathMetadata metadata, PathInits inits) {
        this(FitnessstudioInventoryItem.class, metadata, inits);
    }

    public QFitnessstudioInventoryItem(Class<? extends FitnessstudioInventoryItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new org.salespointframework.inventory.QMultiInventoryItem(type, metadata, inits);
        this.inventoryItemIdentifier = _super.inventoryItemIdentifier;
        this.product = inits.isInitialized("product") ? new org.salespointframework.catalog.QProduct(forProperty("product"), inits.get("product")) : null;
        this.quantity = _super.quantity;
    }

}

