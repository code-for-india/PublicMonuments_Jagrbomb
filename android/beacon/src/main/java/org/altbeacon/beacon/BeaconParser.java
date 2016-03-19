package org.altbeacon.beacon;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
//import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dyoung on 7/21/14.
 *
 * <p>A <code>BeaconParser</code> may be used to tell the library how to decode a beacon's fields
 * from a Bluetooth LE advertisement by specifying what byte offsets match what fields, and what
 * byte sequence signifies the beacon.  Defining a parser for a specific beacon type may be handled
 * via subclassing ({@link AltBeaconParser see AltBeaconParser}) or by simply constructing an instance and calling the
 * <code>setLayout</code> method.  Either way, you will then need to tell the BeaconManager about
 * it like so:</p>
 *
 * <pre><code>
 * BeaconManager.getBeaconParsers().add(new BeaconParser()
 *   .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
 * </pre></code>
 *
 * <p>
 * For more information on how to set up parsing of a beacon,
 * {@link #setBeaconLayout(String) see setBeaconLayout(String)}
 * </p>
 *
 */
public class BeaconParser {
    private static final String TAG = "BeaconParser";
    private static final Pattern I_PATTERN = Pattern.compile("i\\:(\\d+)\\-(\\d+)(l?)");
    private static final Pattern M_PATTERN = Pattern.compile("m\\:(\\d+)-(\\d+)\\=([0-9A-F-a-f]+)");
    private static final Pattern D_PATTERN = Pattern.compile("d\\:(\\d+)\\-(\\d+)([bl]?)");
    private static final Pattern P_PATTERN = Pattern.compile("p\\:(\\d+)\\-(\\d+)");
    private static final char[] HEX_ARRAY = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    private boolean  isShowLog = false;

    private Long mMatchingBeaconTypeCode;
    protected List<Integer> mIdentifierStartOffsets;
    protected List<Integer> mIdentifierEndOffsets;
    protected List<Boolean> mIdentifierLittleEndianFlags;
    protected List<Integer> mDataStartOffsets;
    protected List<Integer> mDataEndOffsets;
    protected List<Boolean> mDataLittleEndianFlags;
    protected Integer mMatchingBeaconTypeCodeStartOffset;
    protected Integer mMatchingBeaconTypeCodeEndOffset;
    protected Integer mPowerStartOffset;
    protected Integer mPowerEndOffset;

    /**
     * Makes a new BeaconParser.  Should normally be immediately followed by a call to #setLayout
     */
    public BeaconParser() {
        mIdentifierStartOffsets = new ArrayList<Integer>();
        mIdentifierEndOffsets = new ArrayList<Integer>();
        mDataStartOffsets = new ArrayList<Integer>();
        mDataEndOffsets = new ArrayList<Integer>();
        mDataLittleEndianFlags = new ArrayList<Boolean>();
        mIdentifierLittleEndianFlags = new ArrayList<Boolean>();
    }

    /**
     * <p>Defines a beacon field parsing algorithm based on a string designating the zero-indexed
     * offsets to bytes within a BLE advertisement.</p>
     *
     * <p>If you want to see examples of how other folks have set up BeaconParsers for different
     * kinds of beacons, try doing a Google search for "getBeaconParsers" (include the quotes in
     * the search.)</p>
     *
     * <p>Four prefixes are allowed in the string:</p>
     *
     * <pre>
     *   m - matching byte sequence for this beacon type to parse (exactly one required)
     *   i - identifier (at least one required, multiple allowed)
     *   p - power calibration field (exactly one required)
     *   d - data field (optional, multiple allowed)
     * </pre>
     *
     * <p>Each prefix is followed by a colon, then an inclusive decimal byte offset for the field from
     * the beginning of the advertisement.  In the case of the m prefix, an = sign follows the byte
     * offset, followed by a big endian hex representation of the bytes that must be matched for
     * this beacon type.  When multiple i or d entries exist in the string, they will be added in
     * order of definition to the identifier or data array for the beacon when parsing the beacon
     * advertisement.  Terms are separated by commas.</p>
     *
     * <p>All offsets from the start of the advertisement are relative to the first byte of the
     * two byte manufacturer code.  The manufacturer code is therefore always at position 0-1</p>
     *
     * <p>All data field and identifier expressions may be optionally suffixed with the letter l, which
     * indicates the field should be parsed as little endian.  If not present, the field will be presumed
     * to be big endian.
     *
     * <p>If the expression cannot be parsed, a <code>BeaconLayoutException</code> is thrown.</p>
     *
     * <p>Example of a parser string for AltBeacon:</p>
     *
     * </pre>
     *   "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"
     * </pre>
     *
     * <p>This signifies that the beacon type will be decoded when an advertisement is found with
     * 0xbeac in bytes 2-3, and a three-part identifier will be pulled out of bytes 4-19, bytes
     * 20-21 and bytes 22-23, respectively.  A signed power calibration value will be pulled out of
     * byte 24, and a data field will be pulled out of byte 25.</p>
     *
     * Note: bytes 0-1 of the BLE manufacturer advertisements are the two byte manufacturer code.
     * Generally you should not match on these two bytes when using a BeaconParser, because it will
     * limit your parser to matching only a transmitter made by a specific manufacturer.  Software
     * and operating systems that scan for beacons typically ignore these two bytes, allowing beacon
     * manufacturers to use their own company code assigned by Bluetooth SIG.  The default parser
     * implementation will already pull out this company code and store it in the
     * beacon.mManufacturer field.  Matcher expressions should therefore start with "m2-3:" followed
     * by the multi-byte hex value that signifies the beacon type.
     *
     * @param beaconLayout
     * @return the BeaconParser instance
     */
    public BeaconParser setBeaconLayout(String beaconLayout) {

        String[] terms =  beaconLayout.split(",");

        for (String term : terms) {
            boolean found = false;

            Matcher matcher = I_PATTERN.matcher(term);
            while (matcher.find()) {
                found = true;
                try {
                    int startOffset = Integer.parseInt(matcher.group(1));
                    int endOffset = Integer.parseInt(matcher.group(2));
                    Boolean littleEndian = matcher.group(3).equals("l");
                    mIdentifierLittleEndianFlags.add(littleEndian);
                    mIdentifierStartOffsets.add(startOffset);
                    mIdentifierEndOffsets.add(endOffset);
                } catch (NumberFormatException e) {
                    throw new BeaconLayoutException("Cannot parse integer byte offset in term: " + term);
                }
            }
            matcher = D_PATTERN.matcher(term);
            while (matcher.find()) {
                found = true;
                try {
                    int startOffset = Integer.parseInt(matcher.group(1));
                    int endOffset = Integer.parseInt(matcher.group(2));
                    Boolean littleEndian = matcher.group(3).equals("l");
                    mDataLittleEndianFlags.add(littleEndian);
                    mDataStartOffsets.add(startOffset);
                    mDataEndOffsets.add(endOffset);
                } catch (NumberFormatException e) {
                    throw new BeaconLayoutException("Cannot parse integer byte offset in term: " + term);
                }
            }
            matcher = P_PATTERN.matcher(term);
            while (matcher.find()) {
                found = true;
                try {
                    int startOffset = Integer.parseInt(matcher.group(1));
                    int endOffset = Integer.parseInt(matcher.group(2));
                    mPowerStartOffset=startOffset;
                    mPowerEndOffset=endOffset;
                } catch (NumberFormatException e) {
                    throw new BeaconLayoutException("Cannot parse integer power byte offset in term: " + term);
                }
            }
            matcher = M_PATTERN.matcher(term);
            while (matcher.find()) {
                found = true;
                try {
                    int startOffset = Integer.parseInt(matcher.group(1));
                    int endOffset = Integer.parseInt(matcher.group(2));
                    mMatchingBeaconTypeCodeStartOffset = startOffset;
                    mMatchingBeaconTypeCodeEndOffset = endOffset;
                } catch (NumberFormatException e) {
                    throw new BeaconLayoutException("Cannot parse integer byte offset in term: " + term);
                }
                String hexString = matcher.group(3);
                try {
                    mMatchingBeaconTypeCode = Long.decode("0x"+hexString);
                }
                catch (NumberFormatException e) {
                    throw new BeaconLayoutException("Cannot parse beacon type code: "+hexString+" in term: " + term);
                }
            }
            if (!found) {
                BeaconManager.logDebug(TAG, "cannot parse term "+term);
                throw new BeaconLayoutException("Cannot parse beacon layout term: " + term);
            }
        }
        if (mPowerStartOffset == null || mPowerEndOffset == null) {
            throw new BeaconLayoutException("You must supply a power byte offset with a prefix of 'p'");
        }
        if (mMatchingBeaconTypeCodeStartOffset == null || mMatchingBeaconTypeCodeEndOffset == null) {
            throw new BeaconLayoutException("You must supply a matching beacon type expression with a prefix of 'm'");
        }
        if (mIdentifierStartOffsets.size() == 0 || mIdentifierEndOffsets.size() == 0) {
            throw new BeaconLayoutException("You must supply at least one identifier offset withh a prefix of 'i'");
        }
        return this;
    }


    /**
     * @see #mMatchingBeaconTypeCode
     * @return
     */
    public Long getMatchingBeaconTypeCode() {
        return mMatchingBeaconTypeCode;
    }

    /**
     * Construct a Beacon from a Bluetooth LE packet collected by Android's Bluetooth APIs,
     * including the raw bluetooth device info
     *
     * @param scanData The actual packet bytes
     * @param rssi The measured signal strength of the packet
     * @param device The bluetooth device that was detected
     * @return An instance of a <code>Beacon</code>
     */
    @TargetApi(5)
    public Beacon fromScanData(byte[] scanData, int rssi, BluetoothDevice device) {

        if(isShowLog)
        {
            Log.e("scanData*****",scanData+"");
            Log.e("rssi*****", rssi + "");
            Log.e("device*****",device+"");
        }

        return fromScanData(scanData, rssi, device, new Beacon());
    }

    @TargetApi(5)
    protected Beacon fromScanData(byte[] scanData, int rssi, BluetoothDevice device, Beacon beacon) {

        int startByte = 2;
        boolean patternFound = false;
        byte[] typeCodeBytes = longToByteArray(getMatchingBeaconTypeCode(), mMatchingBeaconTypeCodeEndOffset-mMatchingBeaconTypeCodeStartOffset+1);

        while (startByte <= 5) {
            if (byteArraysMatch(scanData, startByte+mMatchingBeaconTypeCodeStartOffset, typeCodeBytes, 0)) {
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound == false) {
            // This is not an beacon
            BeaconManager.logDebug(TAG, "This is not a matching Beacon advertisement.  (Was expecting "+byteArrayToString(typeCodeBytes)+".  The bytes I see are: "+bytesToHex(scanData));
            return null;
        }
        else {
            //BeaconManager.logDebug(TAG, "This is a recognized beacon advertisement -- "+String.format("%04x", getMatchingBeaconTypeCode())+" seen");
            if(isShowLog) {
                Log.e("rec beacon**", "This is a recognized beacon advertisement -- " + String.format("%04x", getMatchingBeaconTypeCode()) + " seen");
            }
        }

        ArrayList<Identifier> identifiers = new ArrayList<Identifier>();
        for (int i = 0; i < mIdentifierEndOffsets.size(); i++) {
            String idString = byteArrayToFormattedString(scanData, mIdentifierStartOffsets.get(i)+startByte, mIdentifierEndOffsets.get(i)+startByte, mIdentifierLittleEndianFlags.get(i));
            if(isShowLog)
            {
                Log.e("idString",idString);
            }
            identifiers.add(Identifier.parse(idString));
        }
        ArrayList<Long> dataFields = new ArrayList<Long>();
        for (int i = 0; i < mDataEndOffsets.size(); i++) {
            String dataString = byteArrayToFormattedString(scanData, mDataStartOffsets.get(i)+startByte, mDataEndOffsets.get(i)+startByte, mDataLittleEndianFlags.get(i));
            dataFields.add(Long.parseLong(dataString));


            if(isShowLog) {
                Log.e("parsing data****", i + "");
            }
            BeaconManager.logDebug(TAG, "parsing found data field "+i);
            // TODO: error handling needed here on the parse
        }

        int txPower = 0;
        String powerString = byteArrayToFormattedString(scanData, mPowerStartOffset+startByte, mPowerEndOffset+startByte, false);
        txPower = Integer.parseInt(powerString);

        // make sure it is a signed integer
        if (txPower > 127) {
            txPower -= 256;
        }

        if(isShowLog) {
            Log.e("txPower data", txPower + "");

        }/* double batteryLevel = Integer.parseInt((Integer.toBinaryString(scanData[21] & 0xff) + (mDataStartOffsets.get(20)+6) + (mDataStartOffsets.get(20)+ 7)), 2);//TODO: Find a better way to do this.

        //returning mV

      //  Log.d("batteryLIfe",batteryLevel*(3600.00/1024.00)+"");*/


        // TODO: error handling needed on the parse


        int beaconTypeCode = 0;
        String beaconTypeString = byteArrayToFormattedString(scanData, mMatchingBeaconTypeCodeStartOffset+startByte, mMatchingBeaconTypeCodeEndOffset+startByte, false);
        beaconTypeCode = Integer.parseInt(beaconTypeString);
        // TODO: error handling needed on the parse

        int manufacturer = 0;
        String manufacturerString = byteArrayToFormattedString(scanData, startByte, startByte+1, true);
        manufacturer = Integer.parseInt(manufacturerString);

        String macAddress = null;
        String name = null;
        if (device != null) {
            macAddress = device.getAddress();
            name = device.getName();
        }

        beacon.mIdentifiers = identifiers;
        beacon.mDataFields = dataFields;
        beacon.mTxPower = txPower;
        beacon.mRssi = rssi;
        beacon.mBeaconTypeCode = beaconTypeCode;
        beacon.mBluetoothAddress = macAddress;
        beacon.mBluetoothName= name;
        beacon.mManufacturer = manufacturer;

        if(isShowLog) {
            Log.e("manufacturer", manufacturer + "");
            Log.e("mBluetoothName", name + "");
            Log.e("macAddress", macAddress + "");
            Log.e("beaconTypeCode", beaconTypeCode + "");
            Log.e("dataFields", dataFields + "");
        }
        return beacon;
    }

    /**
     * Get BLE advertisement bytes for a Beacon
     * @param beacon the beacon containing the data to be transmitted
     * @return the byte array of the advertisement
     */
    public byte[] getBeaconAdvertisementData(Beacon beacon) {
        byte[] advertisingBytes;

        int lastIndex = -1;
        if (mMatchingBeaconTypeCodeEndOffset != null && mMatchingBeaconTypeCodeEndOffset > lastIndex) {
            lastIndex = mMatchingBeaconTypeCodeEndOffset;
        }
        if (mPowerEndOffset != null && mPowerEndOffset > lastIndex) {
            lastIndex = mPowerEndOffset;
        }
        for (int identifierNum = 0; identifierNum < this.mIdentifierStartOffsets.size(); identifierNum++) {
            if (this.mIdentifierEndOffsets.get(identifierNum) != null && this.mIdentifierEndOffsets.get(identifierNum) > lastIndex) {
                lastIndex = this.mIdentifierEndOffsets.get(identifierNum);
            }
        }
        for (int identifierNum = 0; identifierNum < this.mDataEndOffsets.size(); identifierNum++) {
            if (this.mDataEndOffsets.get(identifierNum) != null && this.mDataEndOffsets.get(identifierNum) > lastIndex) {
                lastIndex = this.mDataEndOffsets.get(identifierNum);
            }
        }

        advertisingBytes = new byte[lastIndex+1-2];
        long beaconTypeCode = this.getMatchingBeaconTypeCode();

        // set type code
        for (int index = this.mMatchingBeaconTypeCodeStartOffset; index <= this.mMatchingBeaconTypeCodeEndOffset; index++) {
            byte value = (byte) (this.getMatchingBeaconTypeCode() >> (8*(this.mMatchingBeaconTypeCodeEndOffset-index)) & 0xff);
            advertisingBytes[index-2] = value;
        }

        // set identifiers
        for (int identifierNum = 0; identifierNum < this.mIdentifierStartOffsets.size(); identifierNum++) {
            byte[] identifierBytes = beacon.getIdentifier(identifierNum).toByteArrayOfSpecifiedEndianness(this.mIdentifierLittleEndianFlags.get(identifierNum));
            for (int index = this.mIdentifierStartOffsets.get(identifierNum); index <= this.mIdentifierEndOffsets.get(identifierNum); index ++) {
                int identifierByteIndex = this.mIdentifierEndOffsets.get(identifierNum)-index;
                if (identifierByteIndex < identifierBytes.length) {
                    advertisingBytes[index-2] = (byte) identifierBytes[this.mIdentifierEndOffsets.get(identifierNum)-index];
                }
                else {
                    advertisingBytes[index-2] = 0;
                }
            }
        }

        // set power
        for (int index = this.mPowerStartOffset; index <= this.mPowerEndOffset; index ++) {
            advertisingBytes[index-2] = (byte) (beacon.getTxPower() >> (8*(index - this.mPowerStartOffset)) & 0xff);
        }

        // set data fields
        for (int dataFieldNum = 0; dataFieldNum < this.mDataStartOffsets.size(); dataFieldNum++) {
            long dataField = beacon.getDataFields().get(dataFieldNum);
            for (int index = this.mDataStartOffsets.get(dataFieldNum); index <= this.mDataEndOffsets.get(dataFieldNum); index ++) {
                int endianCorrectedIndex = index;
                if (this.mDataLittleEndianFlags.get(dataFieldNum)) {
                    endianCorrectedIndex = this.mDataEndOffsets.get(dataFieldNum) - index;
                }
                advertisingBytes[endianCorrectedIndex-2] = (byte) (dataField >> (8*(index - this.mDataStartOffsets.get(dataFieldNum))) & 0xff);
            }
        }

        if(isShowLog)
        {
            Log.e("advertisingBytes*****",advertisingBytes.toString());

            for(int i = 0; i < advertisingBytes.length; i++)
            {
                Log.e("advertisingBytes[i]****",advertisingBytes[i]+"");
            }
        }

        return advertisingBytes;
    }

    public BeaconParser setMatchingBeaconTypeCode(Long typeCode) {
        mMatchingBeaconTypeCode = typeCode;
        return this;
    }

    /**
     * Caclculates the byte size of the specified identifier in this format
     * @param identifierNum
     * @return bytes
     */
    public int getIdentifierByteCount(int identifierNum) {
        return mIdentifierEndOffsets.get(identifierNum) - mIdentifierStartOffsets.get(identifierNum) + 1;
    }


    protected static String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }

        return new String(hexChars);


    }

    public static class BeaconLayoutException extends RuntimeException {
        public BeaconLayoutException(String s) {
	    super(s);
        }
    }

    protected byte[] longToByteArray(long longValue, int length) {
        byte[] array = new byte[length];
        for (int i = 0; i < length; i++){
            //long mask = (long) Math.pow(256.0,1.0*(length-i))-1;
            long mask = 0xffl << (length-i-1)*8;
            long shift = (length-i-1)*8;
            long value = ((longValue & mask)  >> shift);
            //BeaconManager.logDebug(TAG, "masked value is "+String.format("%08x",longValue & mask));
            //BeaconManager.logDebug(TAG, "masked value shifted is "+String.format("%08x",(longValue & mask) >> shift));
            //BeaconManager.logDebug(TAG, "for long "+String.format("%08x",longValue)+" at position: "+i+" of "+length+" mask: "+String.format("%08x",mask)+" shift: "+shift+" the value is "+String.format("%02x",value));
            array[i] = (byte) value;

        }
        return array;
    }
    private boolean byteArraysMatch(byte[] array1, int offset1, byte[] array2, int offset2) {
        int minSize = array1.length > array2.length ? array2.length : array1.length;
        for (int i = 0; i <  minSize; i++) {
            if (array1[i+offset1] != array2[i+offset2]) {
                return false;
            }
        }
        return true;
    }
    private String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x", bytes[i]));
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    private String byteArrayToFormattedString(byte[] byteBuffer, int startIndex, int endIndex, Boolean littleEndian) {
        byte[] bytes = new byte[endIndex-startIndex+1];
        if (littleEndian) {
            for (int i = 0; i <= endIndex-startIndex; i++) {
                bytes[i] = byteBuffer[startIndex+bytes.length-1-i];
            }
        }
        else {
            for (int i = 0; i <= endIndex-startIndex; i++) {
                bytes[i] = byteBuffer[startIndex+i];
            }
        }


        int length = endIndex-startIndex +1;
        // We treat a 1-4 byte number as decimal string
        if (length < 5) {
            Long number = 0l;
            BeaconManager.logDebug(TAG, "Byte array is size "+bytes.length);
            for (int i = 0; i < bytes.length; i++)  {
                BeaconManager.logDebug(TAG, "index is "+i);
                long byteValue = (long) (bytes[bytes.length - i-1] & 0xff);
                long positionValue = (long) Math.pow(256.0,i*1.0);
                long calculatedValue =  (long) (byteValue * positionValue);
                BeaconManager.logDebug(TAG, "calculatedValue for position "+i+" with positionValue "+positionValue+" and byteValue "+byteValue+" is "+calculatedValue);
                number += calculatedValue;
            }
            return number.toString();
        }

        // We treat a 7+ byte number as a hex string
        String hexString = bytesToHex(bytes);

        // And if it is a 12 byte number we add dashes to it to make it look like a standard UUID
        if (bytes.length == 16) {
            StringBuilder sb = new StringBuilder();
            sb.append(hexString.substring(0,8));
            sb.append("-");
            sb.append(hexString.substring(8,12));
            sb.append("-");
            sb.append(hexString.substring(12,16));
            sb.append("-");
            sb.append(hexString.substring(16,20));
            sb.append("-");
            sb.append(hexString.substring(20,32));
            return sb.toString();
        }
        return "0x"+hexString;
    }

}
