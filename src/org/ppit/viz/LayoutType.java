package org.ppit.viz;

/**
 * @brief Enumeration for working with  gephi layout algorithms.
 */
public enum LayoutType {
	
	FORCE_ATLAS           (1, "ForceAtlas"),
	FRUCHTERMAN_REINGOLD  (2, "FruchtermanReingold"),
	YIFANHU_MULTILEVEL    (3, "YifanHu"),
	CIRCULAR_LAYOUT       (4, "Circular"),
	RADIAL_AXIS_LAYOUT    (5, "RadialAxis" );
	
	private int number;
	
	private String name = null;
	
	private LayoutType(int number, String name) {
		this.number = number;
		this.name = name;
	}
	
	public int getNumber () {
		return number;
	}
	
	public String getName() {
		return name;
	}
	
	public static LayoutType getLayoutTypeByNumber(int number) {
		switch (number) {
			case 1:
				return FORCE_ATLAS;
			case 2:
				return FRUCHTERMAN_REINGOLD;
			case 3:
				return YIFANHU_MULTILEVEL;
			case 4:
				return CIRCULAR_LAYOUT;
			case 5:
				return  RADIAL_AXIS_LAYOUT;
			default:
				return null;
			}
	}
}