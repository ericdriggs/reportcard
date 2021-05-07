package com.ericdriggs.reportcard;

@SuppressWarnings("unused")
public class BooleanGenerator {

    /**
     *
     * @param successRatio
     * @return true successRatio of the time.
     * Example successRatios:
     * <dl>
     *     <dt> <=0 </dt><dd>Always returns false</dd>
     *     <dt> .5 </dt><dd>Returns true 50% of the time</dd>
     *     <dt> >=1 </dt>Always returns true<dd></dd>
     * </dl>
     */
    public static boolean generateBoolean(double successRatio) {
        double rnd = random();
        if ( rnd >= (1-successRatio)) {
            return true;
        }
        return false;
    }

    /**
     * @return a random number, equally distributed between [0..1)
     * @see Math#random();
     */
    protected static double random() {
        return Math.random();
    }


}
