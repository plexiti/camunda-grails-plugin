package grails.plugin.camunda

import org.camunda.bpm.engine.ProcessEngine

import java.util.regex.Pattern

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
class CamundaBpmVersion {
    
    protected static String testVersion
    protected static Pattern versionPattern = ~/^(\d+)\.(\d+)\.(\d+)-(alpha|beta|Final|SNAPSHOT)(\d*)$/
    
    public static boolean isAtLeast(String neededVersion) {
        def neededMatcher = versionPattern.matcher(neededVersion)
        if (!neededMatcher.matches()) {
            throw new IllegalArgumentException("Needed version '$neededVersion' does not match expected pattern $versionPattern.")
        }
        def actualVersion = getVersion()
        def actualMatcher = versionPattern.matcher(actualVersion)
        if (!actualMatcher.matches()) {
            throw new UnknownFormatConversionException("camunda BPM version '$actualVersion' does not match expected pattern $versionPattern.")
        }
        def actual = actualMatcher[0][1] as int
        def needed = neededMatcher[0][1] as int
        if (actual > needed) {
            return true
        } else if (actual == needed) {
            actual = actualMatcher[0][2] as int
            needed = neededMatcher[0][2] as int
            if (actual > needed) {
                return true
            } else if (actual == needed) {
                actual = actualMatcher[0][3] as int
                needed = neededMatcher[0][3] as int
                if (actual > needed) {
                    return true
                } else if (actual == needed) {
                    actual = actualMatcher[0][4] as String
                    needed = neededMatcher[0][4] as String
                    if (!actual.equals('SNAPSHOT')) {
                        if (actual.toLowerCase().compareTo(needed.toLowerCase()) > 0) {
                            return true
                        } else if (actual == needed) {
                            actual = (actualMatcher[0][5] ?: 0) as int
                            needed = (neededMatcher[0][5] ?: 0) as int
                            if (actual >= needed) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    public static String getVersion() {
        if (testVersion)
            return testVersion
        ProcessEngine.class.package.implementationVersion ?: "7.0.0-Final"
    }

}
