import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RouteInfo {

	private String routeCode;
	private Character routeDir;
	private Date validFrom;
	private Date validTo;
	private Integer stopCode;
	private Character type;
	private Integer stopOrder;
	private Integer x;
	private Integer y;

	public String getRouteCode() {
		return routeCode;
	}

	public Character getRouteDir() {
		return routeDir;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public Integer getStopCode() {
		return stopCode;
	}

	public Character getType() {
		return type;
	}

	public Integer getStopOrder() {
		return stopOrder;
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	public void setRouteCode(String routeCode) {
		if (!routeCode.isEmpty()) {
			this.routeCode = routeCode;
		}
	}

	public void setRouteDir(String routeDir) {
		if (!routeDir.isEmpty()) {
			this.routeDir = routeDir.charAt(0);
		}
	}

	public void setValidFrom(String validFrom) {
		if (!validFrom.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				this.validFrom = format.parse(validFrom);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public void setValidTo(String validTo) {
		if (!validTo.isEmpty()) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				this.validTo = format.parse(validTo);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public void setStopCode(String stopCode) {
		if (!stopCode.isEmpty()) {
			this.stopCode = Integer.parseInt(stopCode);
		}
	}

	public void setType(String type) {
		if (!type.isEmpty()) {
			this.type = type.charAt(0);
		}
	}

	public void setStopOrder(String stopOrder) {
		if (!stopOrder.isEmpty()) {
			this.stopOrder = Integer.parseInt(stopOrder);
		}
	}

	public void setX(String x) {
		if (!x.isEmpty()) {
			this.x = Integer.parseInt(x);
		}
	}

	public void setY(String y) {
		if (!y.isEmpty()) {
			this.y = Integer.parseInt(y);
		}
	}

}
