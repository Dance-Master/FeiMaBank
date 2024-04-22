import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

//import javax.crypto.spec.PSource;
//import javax.swing.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.io.*;
//import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ClientExecute {        //create individual sockets to send different information
    private String serverAddress;
    private Socket mainSocket = null;
    ClientConnection clientConnection = ClientConnection.getInstance();

    public static ClientExecute instance = new ClientExecute();     //only one instance in a program

    private ClientExecute() {
        //serverAddress = "localhost";     //TODO: this address should be input by users (done
        ClientConnection.getInstance();
        //mainSocket = clientConnection.getMainSocket();
    }

    static ClientExecute getInstance() {
        return instance;
    }

    public void setMainSocket(Socket mainSocket) {
        this.mainSocket = mainSocket;
    }

    void loginMessageSend(String userName, String password) {       //send username and password to server
        try {
            System.out.println("server address:" + serverAddress);
            Socket loginSocket = new Socket(serverAddress, 10002);
            BufferedWriter loginWriter = new BufferedWriter(new OutputStreamWriter(loginSocket.getOutputStream()));
            loginWriter.write(userName + "\n");
            //loginWriter.newLine();
            loginWriter.write(password + "\n");
            //loginWriter.newLine();
            loginWriter.flush();
            loginWriter.close();
            loginSocket.close();
            System.out.println("login send done!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void registerMassageSend(FMPerson personToBeRegistered) {       //send a person's instance to server
        try {
            Socket registerSocket = new Socket(serverAddress, 10003);
            DataOutputStream registerDOS = new DataOutputStream(registerSocket.getOutputStream());
            ObjectOutputStream registerOOS = new ObjectOutputStream(registerDOS);
            registerOOS.writeObject(personToBeRegistered);
            registerOOS.close();
            registerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    FMPerson personalHomePageReceive() {        //receive a person's instance from server
        try {
            Socket homepageReceiveSocket = new Socket(serverAddress, 10004);
            DataInputStream homePageDIS = new DataInputStream(homepageReceiveSocket.getInputStream());
            ObjectInputStream homePageOIS = new ObjectInputStream(homePageDIS);
            FMPerson homepagePerson = (FMPerson) homePageOIS.readObject();
            homepageReceiveSocket.close();
            return homepagePerson;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    void changeMoneySend(Double changeAmount) {     //send a Double to server
        try {
            Socket moneyChangeSocket = new Socket(serverAddress, 10005);
            DataOutputStream moneyChangeDOS = new DataOutputStream(moneyChangeSocket.getOutputStream());
            moneyChangeDOS.writeDouble(changeAmount);
            moneyChangeDOS.flush();
            moneyChangeDOS.close();
            moneyChangeSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void transferAccountSend(String toId, Double amount) {      //send an id and a Double
        try {
            Socket transferSendSocket = new Socket(serverAddress, 10006);
//            DataOutputStream transferDOS = new DataOutputStream(transferSendSocket.getOutputStream());
            System.out.println("toID:" + toId + " Amount:" + amount);
            BufferedWriter transferBufferedWriter = new BufferedWriter(new OutputStreamWriter(transferSendSocket.getOutputStream()));
            transferBufferedWriter.write(toId + "\n");
            transferBufferedWriter.write(amount.toString() + "\n");
            transferBufferedWriter.flush();
            transferBufferedWriter.close();
            transferSendSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void changePersonalInformation(String changeValue) {      //send a String(button that user choose) firstly, then a String or a Date Object
        //String chooseMode = null;
        try {
            Socket changeInformationSocket = new Socket(serverAddress, 10007);
            BufferedWriter changeInformationBufferedWriter = new BufferedWriter(new OutputStreamWriter(changeInformationSocket.getOutputStream()));
            //while (chooseMode == null) {
                //chooseMode = getMethod in GUI
                //sleep for a while if can
            //}

            //changeInformationBufferedWriter.write(chooseMode);

            //if (!chooseMode.equals("birth_date")) {
                //get and send a String
            //} else {
                //send the Date
            //}
            changeInformationBufferedWriter.write(changeValue);
            changeInformationBufferedWriter.flush();
            changeInformationSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void sendPersonToBeImported(ArrayList<FMPerson> personList) {   //send an ArrayList
        try {
            Socket personListSocket = new Socket(serverAddress, 10008);
            DataOutputStream personListDOS = new DataOutputStream(personListSocket.getOutputStream());
            ObjectOutputStream personListOOS = new ObjectOutputStream(personListDOS);
            personListOOS.writeObject(personList);
            personListOOS.close();
            personListSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ArrayList<FMPerson> receivePerson() {       //receive ArrayList<FMPerson>
        ArrayList<FMPerson> tmpList = new ArrayList<FMPerson>();
        try {
            Socket receivePersonSocket = new Socket(serverAddress, 10009);
            DataInputStream personDIS = new DataInputStream(receivePersonSocket.getInputStream());
            BufferedInputStream personBIS = new BufferedInputStream(personDIS);
            ObjectInputStream personOIS = new ObjectInputStream(personBIS);
            tmpList = (ArrayList<FMPerson>) personOIS.readObject();
//            for(int i = 0; i < tmpList.size();i++){
//                tmpList.get(i).printOnePerson();
//            }
            if (tmpList == null) {
                tmpList = new ArrayList<FMPerson>();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tmpList;

    }

    ArrayList<Double> receivePdfReport() {  //receive Arraylist<Double>
        try {
            Socket pdfSocket = new Socket(serverAddress, 10010);
            ObjectInputStream pdfOIS = new ObjectInputStream(new DataInputStream(pdfSocket.getInputStream()));
            return (ArrayList<Double>) pdfOIS.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    void exportPdfToLocation(ArrayList<Double> pdfDouble) {
        System.out.println("total users:"+ pdfDouble.get(0));
        System.out.println("total money:" +pdfDouble.get(1));
        File file = new File("report.pdf");
        if(file.exists()){
            file.delete();
        }
        try{
            file.createNewFile();
            Rectangle tRectangle = new Rectangle(PageSize.A4);
            tRectangle.setBackgroundColor(BaseColor.WHITE);
            tRectangle.setBorder(2000);
            tRectangle.setBorderColor(BaseColor.RED);
            tRectangle.setBorderWidth(244.2f);
            com.itextpdf.text.Document doc = new Document(tRectangle);
            //doc = new Document(tRectangle.rotate());
            PdfWriter writer = PdfWriter.getInstance(doc,new FileOutputStream(file));
            doc.open();
            //writer.setPdfVersion(PdfWriter.VERSION_1_2);
            doc.addTitle("FINANCIAL REPORT (se)");
            doc.addAuthor("Fei Ma Bank");
            Paragraph tParagraph1 = new Paragraph("OUR TOTAL USERS ARE:",FontFactory.getFont(FontFactory.TIMES_ROMAN, 40,Font.NORMAL, BaseColor.BLACK));
            Paragraph tParagraph2 = new Paragraph(  String.valueOf(Math.floor(pdfDouble.get(0)))+"!!",FontFactory.getFont(FontFactory.TIMES_ROMAN, 55,Font.NORMAL, BaseColor.RED));
            Paragraph tParagraph3 = new Paragraph("THE MONEY IN OUR BANK ARE TOTALLY:",FontFactory.getFont(FontFactory.TIMES_ROMAN, 40,Font.NORMAL, BaseColor.BLACK));
            Paragraph tParagraph4 = new Paragraph(pdfDouble.get(1).toString()+" $!!",FontFactory.getFont(FontFactory.TIMES_ROMAN, 55,Font.NORMAL, BaseColor.RED));
            Paragraph tParagraph5 = new Paragraph("THAT IS AMAZING!!!",FontFactory.getFont(FontFactory.TIMES_ROMAN, 25,Font.NORMAL, BaseColor.BLACK));
            tParagraph1.setAlignment(Element.ALIGN_CENTER);
            tParagraph2.setAlignment(Element.ALIGN_CENTER);
            tParagraph3.setAlignment(Element.ALIGN_CENTER);
            tParagraph4.setAlignment(Element.ALIGN_CENTER);
            tParagraph5.setAlignment(Element.ALIGN_CENTER);
            doc.add(tParagraph1);
            doc.add(tParagraph2);
            doc.add(tParagraph3);
            doc.add(tParagraph4);
            doc.add(tParagraph5);
            doc.close();

            //PdfPTable table = new PdfPTable(1);
            //table.setKeepTogether(true);
            //table.setSplitLate(false);

            //PdfPTable tavle

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    void exportXlsToLocation(ArrayList<FMPerson> personInXls) {
        //DataOutputStream dOS = new DataOutputStream()
        try {
            File file = new File("users.xls");
            if (file.exists()) {
                file.delete();  //make sure the old file is deleted
            }
            file.createNewFile();   //create a new file to write in
            WritableWorkbook workbook = Workbook.createWorkbook(new FileOutputStream(file));
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            //创建要显示的内容,创建一个单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
            Label userName = new Label(0, 0, "username");
            sheet.addCell(userName);
            Label password = new Label(1, 0, "password");
            sheet.addCell(password);
            Label numberId = new Label(2, 0, "number id");
            sheet.addCell(numberId);
            Label phoneNumber = new Label(3, 0, "phone number");
            sheet.addCell(phoneNumber);
            Label gender = new Label(4, 0, "gender");
            sheet.addCell(gender);
            Label birthDate = new Label(5, 0, "birth date");
            sheet.addCell(birthDate);
            Label money = new Label(6, 0, "money");
            sheet.addCell(money);
            Label id = new Label(7, 0, "id");
            sheet.addCell(id);
            int total = personInXls.size();
            for (int i = 0; i < total; i++) {
                FMPerson tmp = personInXls.get(i);
                tmp.printOnePerson();
                Label iUserName = new Label(0, i + 1, tmp.getUserName());
                sheet.addCell(iUserName);
                Label iPassword = new Label(1, i + 1, tmp.getPswd());
                sheet.addCell(iPassword);
                Label iNumberId = new Label(2, i + 1, tmp.getNumberId());
                sheet.addCell(iNumberId);
                Label iPhoneNumber = new Label(3, i + 1, tmp.getPhoneNumber());
                sheet.addCell(iPhoneNumber);
                Label iGender = new Label(4, i + 1, tmp.getGender());
                sheet.addCell(iGender);
                Label iBirthDate = new Label(5, i + 1, tmp.getBirthDate().toString());
                sheet.addCell(iBirthDate);
                Label iMoney = new Label(6, i + 1, Double.toString(tmp.getMoney()));
                sheet.addCell(iMoney);
                Label iId = new Label(7, i + 1, tmp.getId());
                sheet.addCell(iId);
            }
            workbook.write();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<FMPerson> importFromXls() {
        ArrayList<FMPerson> personList = new ArrayList<FMPerson>();
        try {
            File file = new File("users.xls");
            if (!file.exists()) {
                System.out.println("file cannot found");
                return personList;
            }
            //FileInputStream fIS = new FileInputStream(file);
            jxl.Workbook wb = Workbook.getWorkbook(file);
            Sheet rs = wb.getSheet(0);
            int i = 0;
            while (true) {
                i++;
                FMPerson tmpPerson = new FMPerson();
                String[] result = new String[10];
                Cell[] cells;
                //for (int j = 0; j < 8; j++) {
                try{
                    cells = rs.getRow(i);
                } catch (ArrayIndexOutOfBoundsException exception){
                    //exception.printStackTrace();
                    break;
                }
                for (int k = 0; k < cells.length; k++) {
                    StringBuilder sB = new StringBuilder();
                    sB.append(cells[k].getContents());
                    result[k] = sB.toString();

                }
                //}
                tmpPerson.setUserName(result[0]);
                tmpPerson.setPswd(result[1]);
                tmpPerson.setNumberId(result[2]);
                tmpPerson.setPhoneNumber(result[3]);
                tmpPerson.setGender(result[4]);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                tmpPerson.setBirthDate(sdf.parse(result[5]));
                //tmpPerson.setBirthDate(result[5]); TODO:string to ???.date
                tmpPerson.setMoney(Double.parseDouble(result[6]));
                tmpPerson.setId(result[7]);
                tmpPerson.printOnePerson();
                if (tmpPerson.getUserName() == null) break;
                personList.add(tmpPerson);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return personList;
        }

    }


    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}