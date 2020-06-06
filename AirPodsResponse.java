package iciclez.airpods;

public class AirPodsResponse {

    private byte[] buffer;

    private int leftAirPodBattery;
    private int rightAirPodBattery;
    private int caseBattery;

    private boolean leftAirPodCharging;
    private boolean rightAirPodCharging;
    private boolean caseCharging;

    public AirPodsResponse(byte[] buffer)
    {
        this.buffer = buffer;

        /*
        * 07 19 01 0F 20 01 A9 0F 03 00 04 17 3D 7C 74 29 9A 88 AC A1 92 6B 3B 16 17 08 49
            D/0xdeadbeef: false false false 10 9 15
            *
        * 07 19 01 0F 20 71 AA 18 01 00 04 F2 4D 96 61 55 37 3B 9E AA 8F 0C 4B 19 BF BE 4A
            true false false 10 10 8
            *
        * 07 19 01 0F 20 75 AA 38 01 00 14 67 79 02 AE 38 49 11 EC EC ED AC 70 8B C2 F9 05
            D/0xdeadbeef: true true false 10 10 8
        * */

        /*

        [6hi] left battery
        [6lo] right battery
        [7hi] charge status
        [7lo] case battery

         */

        this.leftAirPodBattery = (this.buffer[6] & 0xff) >> 4;
        this.rightAirPodBattery = (this.buffer[6] & 0xff)& 0xf;
        int charge = (this.buffer[7] & 0xff) >> 4;
        this.caseBattery = (this.buffer[7] & 0xff) & 0xf;

        this.leftAirPodCharging = (charge & 0b00000001) != 0;
        this.rightAirPodCharging = (charge & 0b00000010) != 0;
        this.caseCharging = (charge & 0b00000100) != 0;

        //debug Log.d("0xdeadbeef", Arrays.toString(this.buffer) + " " + toString())
    }

    public int getLeftAirPodBattery()
    {
        return leftAirPodBattery;
    }

    public int getRightAirPodBattery()
    {
        return rightAirPodBattery;
    }

    public int getCaseBattery()
    {
        return caseBattery;
    }

    public boolean isLeftAirPodCharging()
    {
        return leftAirPodCharging;
    }

    public boolean isRightAirPodCharging()
    {
        return rightAirPodCharging;
    }

    public boolean isCaseCharging()
    {
        return caseCharging;
    }

    @Override
    public String toString() {
        return "AirPodsResponse{" +
                "leftAirPodBattery=" + leftAirPodBattery +
                ", rightAirPodBattery=" + rightAirPodBattery +
                ", caseBattery=" + caseBattery +
                ", leftAirPodCharging=" + leftAirPodCharging +
                ", rightAirPodCharging=" + rightAirPodCharging +
                ", caseCharging=" + caseCharging +
                '}';
    }
}
