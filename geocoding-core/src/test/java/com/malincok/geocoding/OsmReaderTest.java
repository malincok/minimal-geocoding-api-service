package com.malincok.geocoding;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class OsmReaderTest {

    //test data
    /*
        //<node id="21312786" timestamp="2026-08-15T05:47:00Z" lat="49.1904544" lon="16.6131745">
            //<tag k="name" v="Brno hl.n."/>
        //<node id="21496199" timestamp="2026-08-15T05:47:00Z" lat="49.1906045" lon="16.6130691">
            //<tag k="name" v="Brno hl.n."/
        //<node id="83365417" timestamp="2026-03-25T15:41:49Z" lat="49.1951704" lon="16.6096238">
            //<tag k="name" v="Knihovna Jiřího Mahena"/>
            //<tag k="old_name" v="Schrattenbachův palác"/>
        //<node id="98989898" timestamp="2026-03-25T15:41:49Z" lat="49.1951704" lon="16.6096238">
            //<tag k="old_name" v="Schrattenbachův palác"/>
        //<node id="83483337" timestamp="2026-05-31T15:11:50Z" lat="49.1976369" lon="16.6081832">
            //<tag k="name" v="Místodržitelský palác"/>
            //<tag k="name:es" v="Palacio del Gobernador"/>
            //<tag k="old_name" v="Augustiniánský klášter"/>
        //<node id="99999999" timestamp="2026-05-31T15:11:50Z" lat="49.1976369" lon="16.6081832">
            //<tag k="name" v="Místodržitelský palác"/>
            //<tag k="name:es" v="Palacio del Gobernador"/>
            //<tag k="old_name" v="Augustiniánský klášter"/>
        //<node id="292055487" timestamp="2025-05-07T18:16:37Z" lat="49.1950266" lon="16.6147778">
            //<tag k="name" v="Malinovského náměstí"/>
        //<node id="123456789" timestamp="2025-05-08T18:16:37Z" lat="49.1950266" lon="16.6147778">
            //<tag k="name" v="Malinovského náměstí"/>
        //<node id="1601566699" timestamp="2026-01-21T10:45:29Z" lat="49.1922443" lon="16.6113382">
            //<tag k="name" v="Brno"/>
            //<tag k="name:am" v="ብርኖ"/>
            //<tag k="name:ar" v="برنو"/>
            //<tag k="name:be" v="Брно"/>
            //<tag k="name:bg" v="Бърно"/>
            //<tag k="name:ce" v="Брно"/>
            //<tag k="name:cs" v="Brno"/>
            //<tag k="name:de" v="Brünn"/>
            //<tag k="name:el" v="Μπρνο"/>
            //<tag k="name:fa" v="برنو"/>
            //<tag k="name:he" v="ברנו"/>
            //<tag k="name:hi" v="ब्र्नो"/>
            //<tag k="name:hu" v="Brno"/>
            //<tag k="name:hy" v="Բռնո"/>
            //<tag k="name:ja" v="ブルノ"/>
            //<tag k="name:ka" v="ბრნო"/>
            //<tag k="name:kk" v="Брно"/>
            //<tag k="name:kn" v="ಬೃನೋ"/>
            //<tag k="name:ko" v="브르노"/>
            //<tag k="name:kv" v="Брно"/>
            //<tag k="name:ky" v="Брно"/>
            //<tag k="name:la" v="Bruna"/>
            //<tag k="name:lt" v="Brno"/>
            //<tag k="name:mk" v="Брно"/>
            //<tag k="name:mn" v="Брно"/>
            //<tag k="name:mr" v="ब्रनो"/>
            //<tag k="name:os" v="Брно"/>
            //<tag k="name:ps" v="برنو"/>
            //<tag k="name:ru" v="Брно"/>
            //<tag k="name:sk" v="Brno"/>
            //<tag k="name:sq" v="Bërno"/>
            //<tag k="name:sr" v="Брно"/>
            //<tag k="name:ta" v="பிர்னோ"/>
            //<tag k="name:tg" v="Брно"/>
            //<tag k="name:th" v="เบอร์โน"/>
            //<tag k="name:tt" v="Брно"/>
            //<tag k="name:uk" v="Брно"/>
            //<tag k="name:ur" v="برنو"/>
            //<tag k="name:yi" v="ברין"/>
            //<tag k="name:zh" v="布爾諾"/>
            //<tag k="old_name:hu" v="Berén"/>
        //<node id="3325029085" timestamp="2026-08-15T10:37:22Z" lat="49.1911221" lon="16.6131116">
            //<tag k="name" v="Brno hlavní nádraží"/>
            //<tag k="name:de" v="Brünn Hauptbahnhof"/>
            //<tag k="name:pl" v="Brno Główne"/>
            //<tag k="name:ru" v="Брно Главни Надражи"/>
     */

    @Test
    public void test() {
        List<Poi> results = OsmReader.readData(getClass().getResource("/test.osm"));
        Assert.assertNotNull(results);
        Assert.assertEquals(10, results.size());
        Assert.assertTrue(results.stream().noneMatch(poi -> poi.getNames().isEmpty()));
    }
}
