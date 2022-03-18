package com.example.form;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 
 * @author hondayuki
 *
 */
public class UpdateOrderForm {
    /** ID */
    private Integer id;
	/** 宛先氏名 */
    @NotBlank(message = "名前を入力して下さい")
	private String destinationName;
	/** 宛先Eメール */
    @NotBlank(message = "メールアドレスを入力して下さい")
    @Email(message = "メールアドレスの形式が不正です")
	private String destinationEmail;
	/** 宛先郵便番号 */
    @NotBlank(message = "郵便番号を入力して下さい")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{4}$", message = "郵便番号はXXX-XXXXの形式で入力してください")
	private String destinationZipcode;
	/** 宛先住所 */
    @NotBlank(message = "住所を入力して下さい")
	private String destinationAddress;
	/** 宛先TEL */
    @NotBlank(message = "電話番号を入力して下さい")
    @Pattern(regexp = "^[0-9]{2,4}-[0-9]{2,4}-[0-9]{4}$", message = "電話番号はXXXX-XXXX-XXXXの形式で入力してください")
	private String destinationTel;
	/** 配達日 */
    @NotBlank(message = "配達日時を入力して下さい")
	private String deliveryDate;
    /** 配達時間 */
	private String deliveryTime;
    /** 支払方法 */
    private String paymentMethod;

    public Integer getIntPaymentmethod() {
        return Integer.parseInt(paymentMethod);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getDestinationEmail() {
        return destinationEmail;
    }

    public void setDestinationEmail(String destinationEmail) {
        this.destinationEmail = destinationEmail;
    }

    public String getDestinationZipcode() {
        return destinationZipcode;
    }

    public void setDestinationZipcode(String destinationZipcode) {
        this.destinationZipcode = destinationZipcode;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getDestinationTel() {
        return destinationTel;
    }

    public void setDestinationTel(String destinationTel) {
        this.destinationTel = destinationTel;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "UpdateOrderForm [deliveryDate=" + deliveryDate + ", deliveryTime=" + deliveryTime
                + ", destinationAddress=" + destinationAddress + ", destinationEmail=" + destinationEmail
                + ", destinationName=" + destinationName + ", destinationTel=" + destinationTel
                + ", destinationZipcode=" + destinationZipcode + ", id=" + id + ", paymentMethod=" + paymentMethod
                + "]";
    }

    

    

}
