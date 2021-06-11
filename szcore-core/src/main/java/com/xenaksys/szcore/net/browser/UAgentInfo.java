package com.xenaksys.szcore.net.browser;

import javax.servlet.http.HttpServletRequest;

import static com.xenaksys.szcore.net.browser.BrowserOS.ANDROID;
import static com.xenaksys.szcore.net.browser.BrowserOS.IOS;
import static com.xenaksys.szcore.net.browser.BrowserOS.LINUX;
import static com.xenaksys.szcore.net.browser.BrowserOS.MACOS;
import static com.xenaksys.szcore.net.browser.BrowserOS.WINDOWS;
import static com.xenaksys.szcore.net.browser.BrowserType.CHROME;
import static com.xenaksys.szcore.net.browser.BrowserType.CHROMIUM;
import static com.xenaksys.szcore.net.browser.BrowserType.EDGE;
import static com.xenaksys.szcore.net.browser.BrowserType.FIREFOX;
import static com.xenaksys.szcore.net.browser.BrowserType.IE;
import static com.xenaksys.szcore.net.browser.BrowserType.OPERA;
import static com.xenaksys.szcore.net.browser.BrowserType.SAFARI;
import static com.xenaksys.szcore.net.browser.BrowserType.SAMSUNG;

public class UAgentInfo {
    // User-Agent and Accept HTTP request headers
    private String userAgent = "";

    // Initialize some initial smartphone string variables.
    public static final String engineWebKit = "webkit";
    public static final String deviceAndroid = "android";
    public static final String deviceIphone = "iphone";
    public static final String deviceIpod = "ipod";
    public static final String deviceIpad = "ipad";
    public static final String deviceSymbian = "symbian";
    public static final String deviceS60 = "series60";
    public static final String deviceS70 = "series70";
    public static final String deviceS80 = "series80";
    public static final String deviceS90 = "series90";
    public static final String deviceWinMob = "windows ce";
    public static final String deviceWindows = "windows";
    public static final String deviceIeMob = "iemobile";
    public static final String enginePie = "wm5 pie"; //An old Windows Mobile
    public static final String deviceBB = "blackberry";
    public static final String vndRIM = "vnd.rim"; //Detectable when BB devices emulate IE or Firefox
    public static final String deviceBBStorm = "blackberry95";  //Storm 1 and 2
    public static final String devicePalm = "palm";
    public static final String deviceWebOS = "webos"; //For Palm's new WebOS devices

    public static final String engineBlazer = "blazer"; //Old Palm
    public static final String engineXiino = "xiino"; //Another old Palm

    //Initialize variables for mobile-specific content.
    public static final String vndwap = "vnd.wap";
    public static final String wml = "wml";

    //Initialize variables for other random devices and mobile browsers.
    public static final String deviceBrew = "brew";
    public static final String deviceDanger = "danger";
    public static final String deviceHiptop = "hiptop";
    public static final String devicePlaystation = "playstation";
    public static final String deviceNintendoDs = "nitro";
    public static final String deviceNintendo = "nintendo";
    public static final String deviceWii = "wii";
    public static final String deviceXbox = "xbox";
    public static final String deviceArchos = "archos";

    public static final String engineOpera = "opera"; //Popular browser
    public static final String engineTrident = "trident"; //IE EDGE
    public static final String engineNetfront = "netfront"; //Common embedded OS browser
    public static final String engineUpBrowser = "up.browser"; //common on some phones
    public static final String engineOpenWeb = "openweb"; //Transcoding by OpenWave server
    public static final String deviceMidp = "midp"; //a mobile Java technology
    public static final String uplink = "up.link";

    public static final String devicePda = "pda"; //some devices report themselves as PDAs
    public static final String mini = "mini";  //Some mobile browsers put "mini" in their names.
    public static final String mobile = "mobile"; //Some mobile browsers put "mobile" in their user agent strings.
    public static final String mobi = "mobi"; //Some mobile browsers put "mobi" in their user agent strings.

    //Use Maemo, Tablet, and Linux to test for Nokia"s Internet Tablets.
    public static final String maemo = "maemo";
    public static final String maemoTablet = "tablet";
    public static final String qtembedded = "qt embedded"; //for Sony Mylo
    public static final String mylocom2 = "com2"; //for Sony Mylo also

    //In some UserAgents, the only clue is the manufacturer.
    public static final String manuSonyEricsson = "sonyericsson";
    public static final String manuericsson = "ericsson";
    public static final String manuSamsung1 = "sec-sgh";
    public static final String manuSony = "sony";

    //In some UserAgents, the only clue is the operator.
    public static final String svcDocomo = "docomo";
    public static final String svcKddi = "kddi";
    public static final String svcVodafone = "vodafone";

    // Standard desktop browser detection strings
    public static final String msie = "msie";
    public static final String edge = "edge";
    public static final String edg = "edg";
    public static final String msie60 = "msie 6.0";
    public static final String msie61 = "msie 6.1";
    public static final String msie7 = "msie 7.0";
    public static final String msie8 = "msie 8.0";
    public static final String msie9 = "msie 9.0";
    public static final String firefox = "firefox";
    public static final String seamonkey = "seamonkey";
    public static final String safariApple = "apple";
    public static final String safari = "safari";
    public static final String chrome = "chrome";
    public static final String chromium = "chromium";
    public static final String operaPresto = "presto";
    public static final String opera = "opera";
    public static final String operaMobile = "opera mobi";
    public static final String opr = "opr";
    public static final String samsung = "samsung";

    // OS Detection
    public static final String windows = "windows";
    public static final String linux = "linux";
    public static final String macos = "mac os";

    /**
     * Initialize the userAgent and httpAccept variables
     *
     * @param userAgent the User-Agent header
     */
    public UAgentInfo(String userAgent) {
        if (userAgent != null) {
            this.userAgent = userAgent.toLowerCase();
        }
    }

    /**
     * Initialize the userAgent and httpAccept variables by getting the headers
     * from the HttpServletRequest
     *
     * @param request the HttpServletRequest to get the header information from
     */
    public UAgentInfo(HttpServletRequest request) {
        this(request.getHeader("User-Agent"));
    }

    /**
     * Return the lower case HTTP_USER_AGENT
     */
    public String getUserAgent() {
        return userAgent;
    }


    /**
     * Detects if the current device is an iPhone.
     */
    public boolean detectIphone() {
        // The iPod touch says it's an iPhone! So let's disambiguate.
        return userAgent.contains(deviceIphone) && !detectIpod();
    }

    /**
     * Detects if the current device is an iPod Touch.
     */
    public boolean detectIpod() {
        return userAgent.contains(deviceIpod);
    }

    public boolean detectIpad() {
        return userAgent.contains(deviceIpad);
    }

    /**
     * Detects if the current device is an iPhone or iPod Touch.
     */
    public boolean detectAppleMobileDevice() {
        //We repeat the searches here because some iPods may report themselves as an iPhone, which would be okay.
        return userAgent.contains(deviceIphone) || userAgent.contains(deviceIpad) || userAgent.contains(deviceIpod);
    }

    /**
     * Detects if the current device is an Android OS-based device.
     */
    public boolean detectAndroid() {
        return userAgent.contains(deviceAndroid);
    }

    /**
     * Detects if the current device is an Android OS-based device and
     * the browser is based on WebKit.
     */
    public boolean detectAndroidWebKit() {
        return detectAndroid() && detectWebkit();
    }

    /**
     * Detects if the current browser is based on WebKit.
     */
    public boolean detectWebkit() {
        return userAgent.contains(engineWebKit);
    }

    /**
     * Detects if the current browser is the S60 Open Source Browser.
     */
    public boolean detectS60OssBrowser() {
        //First, test for WebKit, then make sure it's either Symbian or S60.
        return detectWebkit() && (userAgent.contains(deviceSymbian) || userAgent.contains(deviceS60));
    }

    /**
     * Detects if the current device is any Symbian OS-based device,
     * including older S60, Series 70, Series 80, Series 90, and UIQ,
     * or other browsers running on these devices.
     */
    public boolean detectSymbianOS() {
        return userAgent.contains(deviceSymbian) || userAgent.contains(deviceS60) ||
                userAgent.contains(deviceS70) || userAgent.contains(deviceS80) ||
                userAgent.contains(deviceS90);
    }

    /**
     * Detects if the current browser is a Windows Mobile device.
     */
    public boolean detectWindowsMobile() {
        //Most devices use 'Windows CE', but some report 'iemobile'
        //  and some older ones report as 'PIE' for Pocket IE.
        return userAgent.contains(deviceWinMob) ||
                userAgent.contains(deviceIeMob) ||
                userAgent.contains(enginePie) ||
                userAgent.contains(deviceWindows);
    }

    /**
     * Detects if the current browser is a BlackBerry of some sort.
     */
    public boolean detectBlackBerry() {
        return userAgent.contains(deviceBB);
    }

    /**
     * Detects if the current browser is a BlackBerry Touch
     * device, such as the Storm
     */
    public boolean detectBlackBerryTouch() {
        return userAgent.contains(deviceBBStorm);
    }

    /**
     * Detects if the current browser is on a PalmOS device.
     */
    public boolean detectPalmOS() {
        //Most devices nowadays report as 'Palm', but some older ones reported as Blazer or Xiino.
        if (userAgent.contains(devicePalm) || userAgent.contains(engineBlazer) ||
                userAgent.contains(engineXiino) && !detectPalmWebOS()) {
            //Make sure it's not WebOS first
            if (detectPalmWebOS()) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Detects if the current browser is on a Palm device
     * running the new WebOS.
     */
    public boolean detectPalmWebOS() {
        return userAgent.contains(deviceWebOS);
    }

    /**
     * Check to see whether the device is any device
     * in the 'smartphone' category.
     */
    public boolean detectSmartphone() {
        return (detectAppleMobileDevice() ||
                detectS60OssBrowser() ||
                detectSymbianOS() ||
                detectWindowsMobile() ||
                detectBlackBerry() ||
                detectPalmOS() ||
                detectPalmWebOS() ||
                detectAndroid());
    }

    /**
     * Detects whether the device is a Brew-powered device.
     */
    public boolean detectBrewDevice() {
        return userAgent.contains(deviceBrew);
    }

    /**
     * Detects the Danger Hiptop device.
     */
    public boolean detectDangerHiptop() {
        return userAgent.contains(deviceDanger) || userAgent.contains(deviceHiptop);
    }

    /**
     * Detects Opera Mobile or Opera Mini.
     * Added by AHand
     */
    public boolean detectOperaMobile() {
        return userAgent.contains(engineOpera) && (userAgent.contains(mini) || userAgent.contains(mobi));
    }

    /**
     * The quick way to detect for a mobile device.
     * Will probably detect most recent/current mid-tier Feature Phones
     * as well as smartphone-class devices.
     */
    public boolean detectMobileQuick() {
        //Ordered roughly by market share, WAP/XML > Brew > Smartphone.
        if (detectBrewDevice()) {
            return true;
        }

        // Updated by AHand
        if (detectOperaMobile()) {
            return true;
        }

        if (userAgent.contains(engineUpBrowser)) {
            return true;
        }
        if (userAgent.contains(engineOpenWeb)) {
            return true;
        }
        if (userAgent.contains(deviceMidp)) {
            return true;
        }

        if (detectSmartphone()) {
            return true;
        }
        if (detectDangerHiptop()) {
            return true;
        }

        if (detectMidpCapable()) {
            return true;
        }

        if (userAgent.contains(devicePda)) {
            return true;
        }
        if (userAgent.contains(mobile)) {
            return true;
        }

        //detect older phones from certain manufacturers and operators.
        if (userAgent.contains(uplink)) {
            return true;
        }
        if (userAgent.contains(manuSonyEricsson)) {
            return true;
        }
        if (userAgent.contains(manuericsson)) {
            return true;
        }
        if (userAgent.contains(manuSamsung1)) {
            return true;
        }
        if (userAgent.contains(svcDocomo)) {
            return true;
        }
        if (userAgent.contains(svcKddi)) {
            return true;
        }
        if (userAgent.contains(svcVodafone)) {
            return true;
        }

        return false;
    }

    public boolean detectMobile() {
        return userAgent.contains(mobile) || userAgent.contains(operaMobile);
    }

    /**
     * Detects if the current device is a Sony Playstation.
     */
    public boolean detectSonyPlaystation() {
        return userAgent.contains(devicePlaystation);
    }

    /**
     * Detects if the current device is a Nintendo game device.
     */
    public boolean detectNintendo() {
        return userAgent.contains(deviceNintendo) || userAgent.contains(deviceWii) ||
                userAgent.contains(deviceNintendoDs);
    }

    /**
     * Detects if the current device is a Microsoft Xbox.
     */
    public boolean detectXbox() {
        return userAgent.contains(deviceXbox);
    }

    /**
     * Detects if the current device is an Internet-capable game console.
     */
    public boolean detectGameConsole() {
        return detectSonyPlaystation() || detectNintendo() || detectXbox();
    }

    /**
     * Detects if the current device supports MIDP, a mobile Java technology.
     */
    public boolean detectMidpCapable() {
        return userAgent.contains(deviceMidp);
    }

    /**
     * Detects if the current device is on one of the Maemo-based Nokia Internet Tablets.
     */
    public boolean detectMaemoTablet() {
        return (userAgent.contains(maemo) || (userAgent.contains(maemoTablet) && userAgent.contains(linux)));
    }

    /**
     * Detects if the current device is an Archos media player/Internet tablet.
     */
    public boolean detectArchos() {
        return userAgent.contains(deviceArchos);
    }

    /**
     * Detects if the current browser is a Sony Mylo device.
     * Updated by AHand
     */
    public boolean detectSonyMylo() {
        return userAgent.contains(manuSony) && (userAgent.contains(qtembedded) ||
                userAgent.contains(mylocom2));
    }

    /**
     * The longer and more thorough way to detect for a mobile device.
     * Will probably detect most feature phones,
     * smartphone-class devices, Internet Tablets,
     * Internet-enabled game consoles, etc.
     * This ought to catch a lot of the more obscure and older devices, also --
     * but no promises on thoroughness!
     */
    public boolean detectMobileLong() {
        return detectMobileQuick() || detectMaemoTablet() || detectGameConsole();
    }

    //*****************************
    // For Desktop Browsers
    //*****************************
    public boolean detectMSIE() {
        return userAgent.contains(msie) || detectTrident();
    }

    public boolean detectTrident() {
        return userAgent.contains(msie);
    }

    public boolean detectEdge() {
        return userAgent.contains(edge) || userAgent.contains(edg);
    }

    public boolean detectMSIE6() {
        return userAgent.contains(msie60) && userAgent.contains(msie61);
    }

    public boolean detectMSIE7() {
        return userAgent.contains(msie7);
    }

    public boolean detectMSIE8() {
        return userAgent.contains(msie8);
    }

    public boolean detectMSIE9() {
        return userAgent.contains(msie9);
    }

    public boolean detectFirefox() {
        return userAgent.contains(firefox) && !detectSeamonkey();
    }

    public boolean detectSeamonkey() {
        return userAgent.contains(seamonkey);
    }

    public boolean detectSafari() {
        return userAgent.contains(safari) && !userAgent.contains(chrome);
    }

    public boolean detectChrome() {
        if (detectSafari()) {
            return false;
        }
        return userAgent.contains(chrome) && !userAgent.contains(chromium) && !userAgent.contains(edge);
    }

    public boolean detectChromium() {
        return userAgent.contains(chromium);
    }

    public boolean detectOpera() {
        return userAgent.contains(operaPresto) || userAgent.contains(opera) || userAgent.contains(opr);
    }

    public boolean detectWindows() {
        return userAgent.contains(windows);
    }

    public boolean detectLinux() {
        return userAgent.contains(linux);
    }

    public boolean detectMacOs() {
        return userAgent.contains(macos);
    }

    public boolean detectSamsung() {
        return userAgent.contains(samsung);
    }

    public BrowserType getBrowserType() {
        if (detectChrome()) {
            return CHROME;
        } else if (detectSafari()) {
            return SAFARI;
        } else if (detectSafari()) {
            return SAFARI;
        } else if (detectFirefox()) {
            return FIREFOX;
        } else if (detectEdge()) {
            return EDGE;
        } else if (detectChromium()) {
            return CHROMIUM;
        } else if (detectSamsung()) {
            return SAMSUNG;
        } else if (detectOpera()) {
            return OPERA;
        } else if (detectMSIE()) {
            return IE;
        }

        return BrowserType.UNKNOWN;
    }

    public boolean isMobile() {
        return detectMobile();
    }

    public BrowserOS getOs() {
        if (detectAndroid()) {
            return ANDROID;
        } else if (detectAppleMobileDevice()) {
            return IOS;
        } else if (detectMacOs()) {
            return MACOS;
        } else if (detectWindows()) {
            return WINDOWS;
        } else if (detectLinux()) {
            return LINUX;
        }
        return BrowserOS.UNKNOWN;
    }

    //*****************************
    // For Mobile Web Site Design
    //*****************************

    /**
     * The quick way to detect for a tier of devices.
     * This method detects for devices which can
     * display iPhone-optimized web content.
     * Includes iPhone, iPod Touch, Android, Palm WebOS, etc.
     */
    public boolean detectTierIphone() {
        return detectAppleMobileDevice() || detectPalmWebOS() || detectAndroid() || detectAndroidWebKit();
    }

    /**
     * The quick way to detect for a tier of devices.
     * This method detects for all smartphones, but
     * excludes the iPhone Tier devices.
     */
    public boolean detectTierSmartphones() {
        return detectSmartphone() && (!detectTierIphone());
    }

    /**
     * The quick way to detect for a tier of devices.
     * This method detects for all other types of phones,
     * but excludes the iPhone and Smartphone Tier devices.
     */
    public boolean detectTierOtherPhones() {
        return detectMobileQuick() && (!detectTierIphone()) && (!detectTierSmartphones());
    }
}