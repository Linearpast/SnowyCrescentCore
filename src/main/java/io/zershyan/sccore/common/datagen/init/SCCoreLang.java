package io.zershyan.sccore.common.datagen.init;

import io.zershyan.sccore.SCCore;

import java.util.ArrayList;
import java.util.List;

public class SCCoreLang {
	public record Lang(String zhCn, String enUs) {}
	public record LangEntity<T>(T key, Lang lang) {
		public LangEntity(T key, String zhCn, String enUs) {
			this(key, new Lang(zhCn, enUs));
		}
	}	public static final List<LangEntity<?>> langList = new ArrayList<>();
	public final static String translationString = "translation." + SCCore.MODID;
	public final static String command = ".command";
	public static final String animation  = ".animation";

    public static void initLang() {
		langList.clear();

		initLangMessage();
	}

	private static void initLangMessage() {
		for (SCCTranslatableLang value : SCCTranslatableLang.values()) {
			langList.add(value.langEntity);
		}
	}
}
