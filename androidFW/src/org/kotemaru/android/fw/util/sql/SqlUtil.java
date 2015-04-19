package org.kotemaru.android.fw.util.sql;

public class SqlUtil {
	public interface Column {
		public int getDbVersion();
		public String name();
		public String type();
	}

	/**
	 * カラム定義から CREATE TABLE 文を生成する。
	 * @param table テーブル名
	 * @param columns カラム定義
	 * @return SQL文
	 */
	public static String getCreateTableDDL(String table, Column[] columns) {
		StringBuilder sbuf = new StringBuilder();
		sbuf.append("CREATE TABLE ").append(table).append('(');
		for (Column column : columns) {
			if (column.getDbVersion() == 0) {
				sbuf.append(column.name()).append(' ').append(column.type()).append(',');
			}
		}
		sbuf.setLength(sbuf.length() - 1);
		sbuf.append(");");
		return sbuf.toString();
	}
	public static String getAlterTableDDL(int dbVer, String table, Column[] columns) {
		StringBuilder sbuf = new StringBuilder();
		sbuf.append("ALTER TABLE ").append(table).append(' ');
		for (Column column : columns) {
			if (column.getDbVersion() == dbVer) {
				sbuf.append(" ADD COLUMN ").append(column.name())
						.append(' ').append(column.type()).append(',');
			}
		}
		sbuf.setLength(sbuf.length() - 1);
		sbuf.append(";");
		return sbuf.toString();
	}
}
