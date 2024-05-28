package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import model.TestModel;
import service.FilterAmount;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		FilterAmount filterAmount = new FilterAmount();
		
		List<TestModel> tests = new ArrayList<>();
		tests.add(new TestModel("รบกวนยกเลิกหักคอมฝ่ายขาขยอด 2085..02 บาทค่ะ", 2085.02));
		tests.add(new TestModel("รฝ/ข 1.07บ.ขอบคุณครับะ", 1.07));
		tests.add(new TestModel("รบกวนยกเลิกหักคอมฝ่ายขายยอด 2417.13 บาทด้วยค่ะ ฝ่ายขายตัดยอดแล้วค่ะ วันที่ 08/05/2563 ขอบคุณค่ะ", 2417.13));
		tests.add(new TestModel("หักคอมฝ่ายขาย .85ะ", 0.85));
		tests.add(new TestModel("ติดต่อเบอร์ 0800710622 หักคอมฝ่ายขาย 102 ย้อนหลัง ณ วันที่ 27/4/2020", 102));
		tests.add(new TestModel("ส.12939 รวม 13900", 13900));
		tests.add(new TestModel("รบกวนโยกเงินไป ID 38119189 จำนวน 1400 บาท", 1400));
		tests.add(new TestModel("หักคอม  ID  41351022  จำนวน  3000  บาท", 3000));
		
		int passCount = 0;
		for (TestModel test : tests) {
			double amount = filterAmount.filter(test.getText());
			
			if (amount == test.getResult()) {
				System.out.println("✔️ Passed => message: " + test.getText());
				passCount += 1;
			} else {
				System.out.println("✖️ Failed");
			}
		}
		
		System.out.println("[" + passCount + "/" + tests.size() + "]" + " tested");
	}
}
