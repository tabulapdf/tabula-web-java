package technology.tabula.tabula_web;

import java.nio.file.Paths;

public class Settings {
	
	public static String getDataDir() {
		
		// when invoking as "java -Dtabula.data_dir=/foo/bar ... -jar tabula.war"
		String dataDir = System.getProperty("tabula.data_dir");
		if (dataDir != null) {
			return new java.io.File(dataDir).getAbsolutePath();
		}
		
	    dataDir = System.getenv("TABULA_DATA_DIR");
	    if (dataDir != null) {
			return new java.io.File(dataDir).getAbsolutePath();
		}
	    
	    // use the usual directory in (system-dependent) user home dir
	    dataDir = null;
	    String osName = System.getProperty("os.name"), home;
	    
	    if (osName.contains("Windows")) {
	        // APPDATA is in a different place (under user.home) depending on
	        // Windows OS version. so use that env var directly, basically
	        String appdata = System.getenv("APPDATA");
	        if (appdata == null) {
	          appdata = java.lang.System.getProperty("user.home");
	        }
	        dataDir = new java.io.File(appdata, "/Tabula").getPath();
	    }
	    else if (osName.contains("Mac")) {
	        home = java.lang.System.getProperty("user.home");
	        dataDir = Paths.get(home, "/Library/Application Support/Tabula").toString();
	    }
	    else { // probably *NIX
	    	// TODO implement *NIX
	    }
	    
	    return dataDir;
	}

}
