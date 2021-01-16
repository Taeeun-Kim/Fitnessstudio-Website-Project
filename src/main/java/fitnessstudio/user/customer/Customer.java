package fitnessstudio.user.customer;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fitnessstudio.contracts.Contract;
import fitnessstudio.user.User;
import fitnessstudio.user.UserAddress;
import org.javamoney.moneta.Money;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderLine;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.time.Interval;
import org.salespointframework.useraccount.UserAccount;

import javax.money.MonetaryAmount;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer extends User {

    @OneToOne(cascade = CascadeType.ALL)
    private Contract contract;

    static final String DATEFORMAT = "dd.MM.yyyy";

    private Money balance;

    public Customer(UserAccount userAccount, UserAddress address, Contract contract) {
        super(userAccount, address);
        this.contract = contract;
        this.balance = Money.of(0, "EUR");
    }

    @SuppressWarnings("unused")
    protected Customer() {}

    public Contract getContract() {
        return contract;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public void addCredit(float credit) {
        this.setBalance(balance.add(Money.of(credit, "EUR")));
    }

    public void subtractCredit(MonetaryAmount credit) {
        this.setBalance(balance.subtract(credit));
    }

    public PdfPTable generatePaymentTable(Interval interval, OrderManager<Order> orderManager)
            throws IOException, DocumentException {

        BaseFont poppinsItalic = BaseFont.createFont("static/assets/fonts/poppins/Poppins-BlackItalic.ttf",
                BaseFont.WINANSI, true);
        BaseFont poppinsBlack = BaseFont.createFont("static/assets/fonts/poppins/Poppins-BoldItalic.ttf",
                BaseFont.WINANSI, true);
        BaseFont sansRegFontBase = BaseFont.createFont("static/assets/fonts/publicsans/static/PublicSans-Regular.ttf",
                BaseFont.WINANSI, true);

        Font paymentFont = new Font(poppinsBlack, 17);
        Font subHeadFont = new Font(poppinsItalic, 20);
        Font normalFont = new Font(sansRegFontBase, 16);

        ArrayList<PdfPCell> paymentInfosList = new ArrayList<>();

        PdfPTable paymentInfos = new PdfPTable(3);
        paymentInfos.setWidthPercentage(100);

        for (int i = 0; i < 3; i++) {
            PdfPCell cell = new PdfPCell();
            cell.setFixedHeight(40f);
            paymentInfosList.add(cell);
        }
        paymentInfosList.get(0).setPhrase(new Phrase("Artikel", subHeadFont));
        paymentInfosList.get(1).setPhrase(new Phrase("Anzahl", subHeadFont));
        paymentInfosList.get(2).setPhrase(new Phrase("Preis", subHeadFont));

        List<Order> orders = orderManager.findBy(getUserAccount()).filter
                (order -> order.getOrderStatus() == OrderStatus.COMPLETED)
                .filter(order -> interval.contains(order.getDateCreated()))
                .toList();
        List<String> articleNames = new ArrayList<>();
        List<Integer> articleNumbers = new ArrayList<>();
        List<Money> articlePrices = new ArrayList<>();
        Money totalPrice = Money.of(0, "EUR");

        if (!orders.isEmpty()) {
            // Namen auffüllen
            for (Order order : orders) {
                for (OrderLine orderline : order.getOrderLines().toList()) {
                    if (!articleNames.contains(orderline.getProductName())) {
                        articleNames.add(orderline.getProductName());
                        articleNumbers.add(0);
                        articlePrices.add(Money.of(0, "EUR"));
                    }
                }
            }
            // Rest auffüllen
            for (Order order : orders) {
                for (OrderLine orderline : order.getOrderLines().toList()) {
                    String articleName = orderline.getProductName();
                    int index = articleNames.indexOf(articleName);
                    articleNumbers.set(index, articleNumbers.get(index)+orderline.getQuantity().getAmount().intValue());
                    articlePrices.set(index, articlePrices.get(index).add(orderline.getPrice()));
                }
            }

            for (int i = 0; i < articleNames.size(); i++) {
                PdfPCell art = new PdfPCell(new Phrase(articleNames.get(i), normalFont));
                PdfPCell num = new PdfPCell(new Phrase(articleNumbers.get(i).toString(), normalFont));
                PdfPCell pr = new PdfPCell(new Phrase(articlePrices.get(i).toString(), normalFont));
                art.setFixedHeight(40f);
                art.setBorder(Rectangle.NO_BORDER);
                num.setFixedHeight(40f);
                num.setBorder(Rectangle.NO_BORDER);
                pr.setFixedHeight(40f);
                pr.setBorder(Rectangle.NO_BORDER);
                paymentInfosList.add(art);
                paymentInfosList.add(num);
                paymentInfosList.add(pr);
            }
        }else{
            for (int i = 0; i < 3; i++) {
                PdfPCell cell = new PdfPCell();
                cell.setFixedHeight(40f);
                cell.setBorder(Rectangle.NO_BORDER);
                paymentInfosList.add(cell);
            }
        }

        for (int i = 0; i < 3; i++) {
            PdfPCell cell = new PdfPCell();
            cell.setFixedHeight(40f);
            cell.setBorder(Rectangle.NO_BORDER);
            paymentInfosList.add(cell);
        }
        if (!orders.isEmpty()) {
            for (Money price : articlePrices) {
                totalPrice = totalPrice.add(price);
            }
        }
        int index = paymentInfosList.size() - 1;
        paymentInfosList.get(index-1).setPhrase(new Phrase("gesamt:", paymentFont));
        paymentInfosList.get(index).setPhrase(new Phrase(totalPrice.toString(), paymentFont));

        paymentInfosList.get(0).setBorder(Rectangle.BOTTOM);
        paymentInfosList.get(1).setBorder(Rectangle.BOTTOM);
        paymentInfosList.get(2).setBorder(Rectangle.BOTTOM);
        paymentInfosList.get(index-3).setBorder(Rectangle.BOTTOM);
        paymentInfosList.get(index-3).setBorderWidthBottom(1f);


        for (PdfPCell e : paymentInfosList) {
            e.setHorizontalAlignment(Element.ALIGN_CENTER);
            e.setVerticalAlignment(Element.ALIGN_MIDDLE);
            paymentInfos.addCell(e);
        }
        return paymentInfos;
    }

    public byte[] generatePdf(BusinessTime time, OrderManager<Order> orderManager)
            throws DocumentException, IOException {

        Document pdf = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(pdf, baos);

        pdf.open();

        BaseFont poppinsItalic = BaseFont.createFont("static/assets/fonts/poppins/Poppins-BlackItalic.ttf",
                BaseFont.WINANSI, true);
        BaseFont sansRegFontBase = BaseFont.createFont("static/assets/fonts/publicsans/static/PublicSans-Regular.ttf",
                BaseFont.WINANSI, true);
        BaseFont dateFontBase = BaseFont.createFont("static/assets/fonts/poppins/Poppins-Light.ttf",
                BaseFont.WINANSI, true);

        Font dateFont = new Font(dateFontBase, 25);
        Font headFont = new Font(poppinsItalic, 30);
        Font subHeadFont = new Font(poppinsItalic, 20);
        Font normalFont = new Font(sansRegFontBase, 16);

        Interval interval = Interval.from(time.getTime().minusMonths(1).withDayOfMonth(1))
                .to(time.getTime().minusMonths(1)
                        .withDayOfMonth(time.getTime().minusMonths(1).toLocalDate().lengthOfMonth()));
        String firstDay = interval.getStart().format(DateTimeFormatter.ofPattern(DATEFORMAT));
        String lastDay = interval.getEnd().format(DateTimeFormatter.ofPattern(DATEFORMAT));

        Image logo = Image.getInstance(getClass().getResource("/static/assets/logo_black.png"));
        logo.scalePercent(15);

        Chunk logoChunk = new Chunk(logo, 0, 0, true);

        Paragraph logoP = new Paragraph();
        logoP.add(logoChunk);
        logoP.setAlignment(Element.ALIGN_CENTER);

        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setFixedHeight(1f);
        lineCell.setBorder(Rectangle.BOTTOM);
        lineCell.setBorderWidthBottom(2f);
        line.addCell(lineCell);

        PdfPTable headTable = new PdfPTable(2);
        headTable.setWidthPercentage(100);
        headTable.setWidths(new float[] {2, 1});

        PdfPCell header = new PdfPCell(new Phrase("Rechnung", headFont));
        PdfPCell date = new PdfPCell(new Phrase(time.getTime().format(DateTimeFormatter.ofPattern(DATEFORMAT)),
                dateFont));

        header.setBorder(Rectangle.NO_BORDER);
        date.setBorder(Rectangle.NO_BORDER);

        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        date.setVerticalAlignment(Element.ALIGN_MIDDLE);
        date.setHorizontalAlignment(Element.ALIGN_RIGHT);

        headTable.addCell(header);
        headTable.addCell(date);

        ArrayList<PdfPCell> infoTableList = new ArrayList<>();

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);

        for (int i = 0; i < 4; i++) {
            PdfPCell e = new PdfPCell();
            e.setFixedHeight(50f);
            infoTableList.add(e);
        }

        infoTableList.get(0).setPhrase(new Phrase("Mitglied:", subHeadFont));
        infoTableList.get(1).setPhrase(new Phrase(getUserAccount().getLastname() + ", "
                + getUserAccount().getFirstname(), normalFont));
        infoTableList.get(2).setPhrase(new Phrase("Abrechnungszeitraum:", subHeadFont));
        infoTableList.get(3).setPhrase(new Phrase(firstDay + " - " + lastDay, normalFont));

        for (PdfPCell e : infoTableList) {
            e.setBorder(Rectangle.NO_BORDER);
            e.setVerticalAlignment(Element.ALIGN_MIDDLE);
            infoTable.addCell(e);
        }

        PdfPTable paymentInfos = generatePaymentTable(interval, orderManager);

        pdf.add(logoP);
        pdf.add(Chunk.NEWLINE);
        pdf.add(line);
        pdf.add(Chunk.NEWLINE);
        pdf.add(headTable);
        pdf.add(Chunk.NEWLINE);
        pdf.add(Chunk.NEWLINE);
        pdf.add(infoTable);
        pdf.add(Chunk.NEWLINE);
        pdf.add(Chunk.NEWLINE);
        pdf.add(paymentInfos);


        pdf.close();
        return baos.toByteArray();
    }
}
