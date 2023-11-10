import org.dom4j.DocumentException;

import java.util.List;

/**
 * 测试类
 */
public class Test {
    public static void main(String[] args) throws DocumentException, InstantiationException, IllegalAccessException {
        String XMLStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Invoice>" +
                "<Head>" +
                "<MsgRef>23242F8B7D80C0A801222BE89ED553F9549D</MsgRef>" +
                "<MsgNo>8120</MsgNo>" +
                "</Head>" +
                "<Msg>" +
                "<Voucher>" +
                "<Details>" +
                "<Item>" +
                "<ItemID>2F8B2D11C0A8012219B36218D06C4201</ItemID>" +
                "<ItemRemark></ItemRemark>" +
                "</Item>" +
                "</Details>" +
                "<Main>" +
                "<Remark></Remark>" +
                "<InvoicingParty>" +
                "<InvoicingPartyCode>232432</InvoicingPartyCode>" +
                "<InvoicingPartyName>哈哈哈</InvoicingPartyName>" +
                "</InvoicingParty>" +
                "<PayerParty>" +
                "<PayerPartyType>1</PayerPartyType>" +
                "<PayerPartyCode>11111111111111111111</PayerPartyCode>" +
                "<PayerPartyName>嘿嘿</PayerPartyName>" +
                "<PhoneNo></PhoneNo>" +
                "</PayerParty>" +
                "</Main>" +
                "</Voucher>" +
                "</Msg>" +
                "</Invoice>";
        Invoice invoice = (Invoice)XMLUtils.XMLToObject(XMLStr, Invoice.class);
        System.out.println(invoice);
    }
}

class Head {
    private String MsgRef;
    private String MsgNo;

    public String getMsgRef() {
        return MsgRef;
    }

    public void setMsgRef(String msgRef) {
        MsgRef = msgRef;
    }

    public String getMsgNo() {
        return MsgNo;
    }

    public void setMsgNo(String msgNo) {
        MsgNo = msgNo;
    }
}

class Msg {
    private Voucher Voucher;

    public Voucher getVoucher() {
        return Voucher;
    }

    public void setVoucher(Voucher voucher) {
        Voucher = voucher;
    }
}
class Voucher {
    private List<Item> Details;
    private Main Main;

    public List<Item> getDetails() {
        return Details;
    }

    public void setDetails(List<Item> details) {
        Details = details;
    }

    public Main getMain() {
        return Main;
    }

    public void setMain(Main main) {
        Main = main;
    }
}

class Item {
    private String ItemID;
    private String ItemRemark;

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public String getItemRemark() {
        return ItemRemark;
    }

    public void setItemRemark(String itemRemark) {
        ItemRemark = itemRemark;
    }
}

class Invoice {
    private Head Head;
    private Msg Msg;

    public Msg getMsg() {
        return Msg;
    }

    public void setMsg(Msg msg) {
        Msg = msg;
    }

    public Head getHead() {
        return Head;
    }

    public void setHead(Head head) {
        Head = head;
    }
}

class InvoicingParty {
    private String InvoicingPartyCode;
    private String InvoicingPartyName;

    public String getInvoicingPartyCode() {
        return InvoicingPartyCode;
    }

    public void setInvoicingPartyCode(String invoicingPartyCode) {
        InvoicingPartyCode = invoicingPartyCode;
    }

    public String getInvoicingPartyName() {
        return InvoicingPartyName;
    }

    public void setInvoicingPartyName(String invoicingPartyName) {
        InvoicingPartyName = invoicingPartyName;
    }
}

class PayerParty {
    private String PayerPartyType;
    private String PayerPartyCode;
    private String PayerPartyName;
    private String PhoneNo;

    public String getPayerPartyType() {
        return PayerPartyType;
    }

    public void setPayerPartyType(String payerPartyType) {
        PayerPartyType = payerPartyType;
    }

    public String getPayerPartyCode() {
        return PayerPartyCode;
    }

    public void setPayerPartyCode(String payerPartyCode) {
        PayerPartyCode = payerPartyCode;
    }

    public String getPayerPartyName() {
        return PayerPartyName;
    }

    public void setPayerPartyName(String payerPartyName) {
        PayerPartyName = payerPartyName;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }
}

class Main {
    private String Remark;
    private InvoicingParty InvoicingParty;
    private PayerParty PayerParty;

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public InvoicingParty getInvoicingParty() {
        return InvoicingParty;
    }

    public void setInvoicingParty(InvoicingParty invoicingParty) {
        InvoicingParty = invoicingParty;
    }

    public PayerParty getPayerParty() {
        return PayerParty;
    }

    public void setPayerParty(PayerParty payerParty) {
        PayerParty = payerParty;
    }
}
