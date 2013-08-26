import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LineInfo {

	private String id;
	private Date date1;
	private Date date2;
	private Character language;
	private String lineName;
	private String terminal1Name;
	private String terminal2Name;
	private Integer stopCodeDir1;
	private Integer stopCodeDir2;
	private Integer lineLengthDir1;
	private Integer lineLengthDir2;
	private Integer transportMean;

	public String getId() {
		return id;
	}

	public Date getDate1() {
		return date1;
	}

	public Date getDate2() {
		return date2;
	}

	public Character getLanguage() {
		return language;
	}

	public String getLineName() {
		return lineName;
	}

	public String getTerminal1Name() {
		return terminal1Name;
	}

	public String getTerminal2Name() {
		return terminal2Name;
	}

	public Integer getStopCodeDir1() {
		return stopCodeDir1;
	}

	public Integer getStopCodeDir2() {
		return stopCodeDir2;
	}

	public Integer getLineLengthDir1() {
		return lineLengthDir1;
	}

	public Integer getLineLengthDir2() {
		return lineLengthDir2;
	}

	public Integer getTransportMean() {
		return transportMean;
	}

	public void setId(String id) {
		if (!id.isEmpty()) {
			this.id = id;
		}
	}

	public void setDate1(String date1) {
		if (!date1.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				this.date1 = format.parse(date1);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public void setDate2(String date2) {
		if (!date2.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				this.date2 = format.parse(date2);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public void setLanguage(String language) {
		if (!language.isEmpty()) {
			this.language = language.charAt(0);
		}
	}

	public void setLineName(String lineName) {
		if (!lineName.isEmpty()) {
			this.lineName = lineName;
		}
	}

	public void setTerminal1Name(String terminal1Name) {
		if (!terminal1Name.isEmpty()) {
			this.terminal1Name = terminal1Name;
		}
	}

	public void setTerminal2Name(String terminal2Name) {
		if (!terminal2Name.isEmpty()) {
			this.terminal2Name = terminal2Name;
		}
	}

	public void setStopCodeDir1(String stopCodeDir1) {
		if (!stopCodeDir1.isEmpty()) {
			this.stopCodeDir1 = Integer.parseInt(stopCodeDir1);
		}
	}

	public void setStopCodeDir2(String stopCodeDir2) {
		if (!stopCodeDir2.isEmpty()) {
			this.stopCodeDir2 = Integer.parseInt(stopCodeDir2);
		}
	}

	public void setLineLengthDir1(String lineLengthDir1) {
		if (!lineLengthDir1.isEmpty()) {
			this.lineLengthDir1 = Integer.parseInt(lineLengthDir1);
		}
	}

	public void setLineLengthDir2(String lineLengthDir2) {
		if (!lineLengthDir2.isEmpty()) {
			this.lineLengthDir2 = Integer.parseInt(lineLengthDir2);
		}
	}

	public void setTransportMean(String transportMean) {
		if (!transportMean.isEmpty()) {
			this.transportMean = Integer.parseInt(transportMean);
		}
	}

}
