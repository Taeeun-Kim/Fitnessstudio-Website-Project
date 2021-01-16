package fitnessstudio.user.employee;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fitnessstudio.schedule.Schedule;
import fitnessstudio.schedule.ScheduleManager;
import fitnessstudio.schedule.employee.holiday.Holiday;
import fitnessstudio.schedule.entry.ScheduleEntry;
import fitnessstudio.user.User;
import fitnessstudio.user.UserAddress;
import fitnessstudio.user.UserManager;
import org.javamoney.moneta.Money;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.time.Interval;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Entity
public class Employee extends User {

    private Money salary;

    static final String DATEFORMAT = "dd.MM.yyyy";

    public Employee(UserAccount userAccount, UserAddress address, Money salary) {
        super(userAccount, address);
        this.salary = salary;

    }

    @SuppressWarnings("unused")
    protected Employee() {}

    public void setSalary(Money salary){
        this.salary = salary;
    }

    public Money getSalary(){
        return salary;
    }

    public byte[] generatePdf(BusinessTime time, ScheduleManager scheduleManager, UserManager userManager)
            throws DocumentException, IOException {
        Document pdf = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(pdf, baos);

        pdf.open();

        BaseFont poppinsItalic = BaseFont.createFont("static/assets/fonts/poppins/Poppins-BlackItalic.ttf",
                BaseFont.WINANSI, true);
        BaseFont poppinsBlack = BaseFont.createFont("static/assets/fonts/poppins/Poppins-BoldItalic.ttf",
                BaseFont.WINANSI, true);
        BaseFont sansRegFontBase = BaseFont.createFont("static/assets/fonts/publicsans/static/PublicSans-Regular.ttf",
                BaseFont.WINANSI, true);
        BaseFont dateFontBase = BaseFont.createFont("static/assets/fonts/poppins/Poppins-Light.ttf",
                BaseFont.WINANSI, true);

        Font dateFont = new Font(dateFontBase, 25);
        Font headFont = new Font(poppinsItalic, 30);
        Font paymentFont = new Font(poppinsBlack, 17);
        Font subHeadFont = new Font(poppinsItalic, 20);
        Font normalFont = new Font(sansRegFontBase, 16);

        Interval interval = Interval.from(time.getTime().minusMonths(1).withDayOfMonth(1))
                .to(time.getTime().minusMonths(1).
                        withDayOfMonth(time.getTime().minusMonths(1).toLocalDate().lengthOfMonth()));
        String firstDay = interval.getStart().format(DateTimeFormatter.ofPattern(DATEFORMAT));
        String lastDay = interval.getEnd().format(DateTimeFormatter.ofPattern(DATEFORMAT));
        Schedule s = new Schedule(Collections.singletonList(this), scheduleManager, userManager, Optional.of(interval));
        Streamable<ScheduleEntry> entries = s.getEntries();

        long hours = 0;
        entries.filter(e -> !(e instanceof Holiday));
        for (ScheduleEntry e : entries) {
            Interval i = Interval.from(e.getStart()).to(e.getEnd());
            hours += i.getDuration().toHours();
        }
        MonetaryAmount payment = salary.multiply(hours);

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

        PdfPCell header = new PdfPCell(new Phrase("Gehaltsabrechnung", headFont));
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

        infoTableList.get(0).setPhrase(new Phrase("Mitarbeiter:", subHeadFont));
        infoTableList.get(1).setPhrase(new Phrase(getUserAccount().getLastname() + ", "
                + getUserAccount().getFirstname(), normalFont));
        infoTableList.get(2).setPhrase(new Phrase("Abrechnungszeitraum:", subHeadFont));
        infoTableList.get(3).setPhrase(new Phrase(firstDay + " - " + lastDay, normalFont));

        for (PdfPCell e : infoTableList) {
            e.setBorder(Rectangle.NO_BORDER);
            e.setVerticalAlignment(Element.ALIGN_MIDDLE);
            infoTable.addCell(e);
        }

        ArrayList<PdfPCell> paymentInfosList = new ArrayList<>();

        PdfPTable paymentInfos = new PdfPTable(3);
        paymentInfos.setWidthPercentage(100);

        for (int i = 0; i < 9; i++) {
            PdfPCell e = new PdfPCell();
            e.setFixedHeight(40f);
            paymentInfosList.add(e);
        }

        paymentInfosList.get(1).setPhrase(new Phrase("Arbeitszeit", subHeadFont));
        paymentInfosList.get(2).setPhrase(new Phrase("Stundenlohn", subHeadFont));
        paymentInfosList.get(4).setPhrase(new Phrase(hours + " Stunden", normalFont));
        paymentInfosList.get(5).setPhrase(new Phrase(salary.toString(), normalFont));
        paymentInfosList.get(7).setPhrase(new Phrase("gesamt:", paymentFont));
        paymentInfosList.get(8).setPhrase(new Phrase(payment.toString(), paymentFont));

        paymentInfosList.get(0).setBorder(Rectangle.BOTTOM);
        paymentInfosList.get(1).setBorder(Rectangle.BOTTOM);
        paymentInfosList.get(2).setBorder(Rectangle.BOTTOM);
        paymentInfosList.get(3).setBorder(Rectangle.NO_BORDER);
        paymentInfosList.get(4).setBorder(Rectangle.NO_BORDER);
        paymentInfosList.get(5).setBorder(Rectangle.BOTTOM);
        paymentInfosList.get(5).setBorderWidthBottom(1f);
        paymentInfosList.get(6).setBorder(Rectangle.NO_BORDER);
        paymentInfosList.get(7).setBorder(Rectangle.NO_BORDER);
        paymentInfosList.get(8).setBorder(Rectangle.NO_BORDER);


        for (PdfPCell e : paymentInfosList) {
            e.setHorizontalAlignment(Element.ALIGN_CENTER);
            e.setVerticalAlignment(Element.ALIGN_MIDDLE);
            paymentInfos.addCell(e);
        }


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
