package htw.berlin.prog2.ha1;

/**
 * Eine Klasse, die das Verhalten des Online Taschenrechners imitiert, welcher auf
 * https://www.online-calculator.com/ aufgerufen werden kann (ohne die Memory-Funktionen)
 * und dessen Bildschirm bis zu zehn Ziffern plus einem Dezimaltrennzeichen darstellen kann.
 * Enthält mit Absicht noch diverse Bugs oder unvollständige Funktionen.
 */
public class Calculator {

    private String screen = "0";

    private double latestValue;

    private String latestOperation = "";

    /**
     * @return den aktuellen Bildschirminhalt als String
     */
    public String readScreen() {
        return screen;
    }

    /**
     * Empfängt den Wert einer gedrückten Zifferntaste. Da man nur eine Taste auf einmal
     * drücken kann muss der Wert positiv und einstellig sein und zwischen 0 und 9 liegen.
     * Führt in jedem Fall dazu, dass die gerade gedrückte Ziffer auf dem Bildschirm angezeigt
     * oder rechts an die zuvor gedrückte Ziffer angehängt angezeigt wird.
     * @param digit Die Ziffer, deren Taste gedrückt wurde
     */
    public void pressDigitKey(int digit) {
        if(digit > 9 || digit < 0) throw new IllegalArgumentException();

        if(screen.equals("0") || latestValue == Double.parseDouble(screen)) screen = "";

        screen = screen + digit;
    }

    /**
     * Empfängt den Befehl der C- bzw. CE-Taste (Clear bzw. Clear Entry).
     * Einmaliges Drücken der Taste löscht die zuvor eingegebenen Ziffern auf dem Bildschirm
     * so dass "0" angezeigt wird, jedoch ohne zuvor zwischengespeicherte Werte zu löschen.
     * Wird daraufhin noch einmal die Taste gedrückt, dann werden auch zwischengespeicherte
     * Werte sowie der aktuelle Operationsmodus zurückgesetzt, so dass der Rechner wieder
     * im Ursprungszustand ist.
     */
    public void pressClearKey() {
        screen = "0";
        latestOperation = "";
        latestValue = 0.0;
    }

    /**
     * Empfängt den Wert einer gedrückten binären Operationstaste, also eine der vier Operationen
     * Addition, Substraktion, Division, oder Multiplikation, welche zwei Operanden benötigen.
     * Beim ersten Drücken der Taste wird der Bildschirminhalt nicht verändert, sondern nur der
     * Rechner in den passenden Operationsmodus versetzt.
     * Beim zweiten Drücken nach Eingabe einer weiteren Zahl wird direkt des aktuelle Zwischenergebnis
     * auf dem Bildschirm angezeigt. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * @param operation "+" für Addition, "-" für Substraktion, "x" für Multiplikation, "/" für Division
     */
    /**
     * Setzt die binäre Operation (+, -, x, /) und speichert den aktuell sichtbaren Wert.
     * Neu: akzeptiert auch "*" oder "×" als Alias für "x".
     */
    public void pressBinaryOperationKey(String operation)  {
        // --- NEU: Aliase auf "x" mappen, damit Multiplikation sicher erkannt wird ---
        if ("*".equals(operation) || "×".equals(operation) || "X".equals(operation)) {
            operation = "x";
        }
        // ---------------------------------------------------------------------------

        latestValue = Double.parseDouble(screen);
        latestOperation = operation;
    }

    /**
     * Empfängt den Wert einer gedrückten unären Operationstaste, also eine der drei Operationen
     * Quadratwurzel, Prozent, Inversion, welche nur einen Operanden benötigen.
     * Beim Drücken der Taste wird direkt die Operation auf den aktuellen Zahlenwert angewendet und
     * der Bildschirminhalt mit dem Ergebnis aktualisiert.
     * @param operation "√" für Quadratwurzel, "%" für Prozent, "1/x" für Inversion
     */
    public void pressUnaryOperationKey(String operation) {
        latestValue = Double.parseDouble(screen);
        latestOperation = operation;
        var result = switch(operation) {
            case "√" -> Math.sqrt(Double.parseDouble(screen));
            case "%" -> Double.parseDouble(screen) / 100;
            case "1/x" -> 1 / Double.parseDouble(screen);
            default -> throw new IllegalArgumentException();
        };
        screen = Double.toString(result);
        if(screen.equals("NaN")) screen = "Error";
        if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);

    }

    /**
     * Empfängt den Befehl der gedrückten Dezimaltrennzeichentaste, im Englischen üblicherweise "."
     * Fügt beim ersten Mal Drücken dem aktuellen Bildschirminhalt das Trennzeichen auf der rechten
     * Seite hinzu und aktualisiert den Bildschirm. Daraufhin eingegebene Zahlen werden rechts vom
     * Trennzeichen angegeben und daher als Dezimalziffern interpretiert.
     * Beim zweimaligem Drücken, oder wenn bereits ein Trennzeichen angezeigt wird, passiert nichts.
     */
    public void pressDotKey() {
        if(!screen.contains(".")) screen = screen + ".";
    }

    /**
     * Empfängt den Befehl der gedrückten Vorzeichenumkehrstaste ("+/-").
     * Zeigt der Bildschirm einen positiven Wert an, so wird ein "-" links angehängt, der Bildschirm
     * aktualisiert und die Inhalt fortan als negativ interpretiert.
     * Zeigt der Bildschirm bereits einen negativen Wert mit führendem Minus an, dann wird dieses
     * entfernt und der Inhalt fortan als positiv interpretiert.
     */
    public void pressNegativeKey() {
        screen = screen.startsWith("-") ? screen.substring(1) : "-" + screen;
    }

    /**
     * Empfängt den Befehl der gedrückten "="-Taste.
     * Wurde zuvor keine Operationstaste gedrückt, passiert nichts.
     * Wurde zuvor eine binäre Operationstaste gedrückt und zwei Operanden eingegeben, wird das
     * Ergebnis der Operation angezeigt. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * Wird die Taste weitere Male gedrückt (ohne andere Tasten dazwischen), so wird die letzte
     * Operation (ggf. inklusive letztem Operand) erneut auf den aktuellen Bildschirminhalt angewandt
     * und das Ergebnis direkt angezeigt.
     */
    /**
     * Empfängt den Befehl der gedrückten "="-Taste.
     * Führt die zuvor gespeicherte Rechenoperation (z. B. +, -, x, /) mit dem aktuell eingegebenen
     * Wert auf dem Bildschirm aus und zeigt das Ergebnis an.
     *
     * In dieser Version wurde die Art geändert, wie das Ergebnis in Textform (also auf dem Bildschirm)
     * angezeigt wird:
     *
     * - Früher wurde das Ergebnis einfach mit Double.toString(result) umgewandelt.
     *   Das führte dazu, dass sehr große oder sehr kleine Zahlen in wissenschaftlicher
     *   Notation angezeigt wurden (z. B. "1.52399025E8" statt "152399025").
     *
     * - Jetzt wird das Ergebnis mit BigDecimal formatiert. Dadurch wird die Zahl immer
     *   "normal" dargestellt, also so wie man sie auf einem Taschenrechner erwarten würde.
     *
     * - Außerdem wird jetzt überprüft, ob das Ergebnis "NaN" (keine Zahl) oder "Infinity"
     *   (z. B. bei Division durch 0) ist. In diesen Fällen zeigt der Rechner "Error" an.
     *
     * - Abschließend wird die Anzeige auf 11 Zeichen begrenzt, damit sie auf den Bildschirm passt.
     */
    /**
     * Führt die zuletzt gesetzte Operation mit dem aktuellen Bildschirmwert aus.
     * Neu:
     * - Division durch 0/NaN -> "Error"
     * - Ergebnis auf 1 Nachkommastelle runden (HALF_UP), z. B. -4.6
     * - Ausgabe ohne Exponentialschreibweise (toPlainString)
     * - "-0" wird zu "0"
     * - Länge auf 11 Zeichen begrenzt (Displaylimit)
     */
    public void pressEqualsKey() {
        // Wenn noch keine Operation gesetzt ist: nichts tun (verhindert Exception)
        if (latestOperation == null || latestOperation.isEmpty()) return;

        // Division durch 0 früh abfangen
        if (latestOperation.equals("/") && Double.parseDouble(screen) == 0.0) {
            screen = "Error";
            return;
        }

        // Rechnen
        double result = switch (latestOperation) {
            case "+" -> latestValue + Double.parseDouble(screen);
            case "-" -> latestValue - Double.parseDouble(screen);
            case "x" -> latestValue * Double.parseDouble(screen);
            case "/" -> latestValue / Double.parseDouble(screen);
            default -> throw new IllegalArgumentException();
        };

        // Fehlerfälle (z. B. 0/0) -> "Error"
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            screen = "Error";
            return;
        }

        // NEU: auf 1 Nachkommastelle runden (kaufmännisch)
        java.math.BigDecimal rounded = java.math.BigDecimal
                .valueOf(result)
                .setScale(1, java.math.RoundingMode.HALF_UP);

        // Lesbar ohne Exponentialschreibweise ausgeben
        screen = rounded.stripTrailingZeros().toPlainString();

        // "-0" vermeiden
        if (screen.equals("-0")) screen = "0";

        // Display-Limit einhalten
        if (screen.length() > 11) screen = screen.substring(0, 11);
    }

}
