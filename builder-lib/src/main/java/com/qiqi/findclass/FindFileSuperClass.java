package com.qiqi.findclass;


import com.qiqi.utils.ByteReader;
import com.qiqi.utils.ByteUtil;
import com.qiqi.utils.FileReader;
import com.qiqi.utils.FileScanHelper;
import com.qiqi.utils.Log;

import java.io.File;
import java.io.IOException;

public class FindFileSuperClass {

    private static final byte Utf8Info_tag = 1;
    private static final byte IntegerInfo_tag = 3;
    private static final byte FloatInfo_tag = 4;
    private static final byte LongInfo_tag = 5;
    private static final byte DoubleInfo_tag = 6;
    private static final byte ClassInfo_tag = 7;
    private static final byte StringInfo_tag = 8;
    private static final byte FieldrefInfo_tag = 9;
    private static final byte MethodrefInfo_tag = 10;
    private static final byte InterfaceMethodrefInfo_tag = 11;
    private static final byte NameAndTypeInfo_tag = 12;
    private static final byte MethodHandleInfo_tag = 15;
    private static final byte MethodTypeInfo_tag = 16;
    private static final byte DynamicInfo_tag = 17;
    private static final byte InvokeDynamicInfo_tag = 18;
    private static final byte ModuleInfo_tag = 19;
    private static final byte PackageInfo_tag = 20;

    private Info[] mInfos;

    private String mPath;

    short mNumOfItems = 1;

    public FindFileSuperClass(String path) {
        mPath = path;
    }

    private void readTest(ByteReader reader) throws IOException {

        println("magic", reader.reads(4));//0xCAFEBABE
        println("minor_version", reader.readShort());
        println("major_version", reader.readShort());
        short n = reader.readShort();
        println("constant_pool_count", n);
        mInfos = new Info[n];
        while (--n > 0) {
            int tag = readConstantPool(reader);
            if ((tag == LongInfo_tag) || (tag == DoubleInfo_tag)) {
                mNumOfItems++;
                --n;
            }
        }
        println("accessFlags", reader.readShort());

        short thisClass = reader.readShort();
        println("thisClass", thisClass);
        short superClass = reader.readShort();
        println("superClass", superClass);
        String name = getClassName(superClass);

        String[] classs;
//        getClassName(thisClass);
        n = reader.readShort();
        if (n == 0) {
            classs = new String[1];
        } else {
            classs = new String[n + 1];
            for (int i = 0; i < n; ++i)
                classs[i + 1] = getClassName(reader.readShort());
        }
        classs[0] = name;
    }

    public String[] safeRead(ByteReader reader)  {
        try {
            return read(reader);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] read(ByteReader reader) throws IOException {
//        println("magic", reader.reads(4));//0xCAFEBABE
        if (reader.readInt() != 0xCAFEBABE){
            return null;
        }
        reader.skip(2 + 2);
        short n = reader.readShort();
        mInfos = new Info[n];
        while (--n > 0) {
            int tag = readConstantPool(reader);
            if ((tag == LongInfo_tag) || (tag == DoubleInfo_tag)) {
                mNumOfItems++;
                --n;
            }
        }
        reader.skip(2);
        String thisClass = getClassName(reader.readShort());
        String superClass = getClassName(reader.readShort());

        String[] classs;
        n = reader.readShort();
        if (n == 0) {
            classs = new String[2];
        } else {
            classs = new String[n + 2];
            for (int i = 0; i < n; ++i)
                classs[i + 1] = getClassName(reader.readShort());
        }
        classs[0] = thisClass;
        classs[1] = superClass;

        return classs;
    }

    private String getClassName(short index) {
        String name = null;
        try {
            ClassInfo classInfo = (ClassInfo) mInfos[index];
            Utf8Info utf8Info = (Utf8Info) mInfos[classInfo.index];
            name = new String(utf8Info.data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public static void println(String name, String value) {
        System.out.println(name + " : " + value);
    }

    public static void println(String name, byte[] value) {
        System.out.println(name + " : 0X" + ByteUtil.byteToHex(value));
    }

    public static void println(String name, short value) {
        System.out.println(name + " : " + value);
        println(name, ByteUtil.short2Byte(value));
    }

    private int readConstantPool(ByteReader in) throws IOException {
        byte tag = in.read();
//        println("  tag", numOfItems + " " + tag);
        switch (tag) {
            case Utf8Info_tag:                     // 1
                Utf8Info utf8Info = new Utf8Info(in, mNumOfItems);
                mInfos[mNumOfItems] = utf8Info;
//                println("   utf8", new String(utf8Info.data));
                break;
            case IntegerInfo_tag:                  // 3
                in.skip(4);
                break;
            case FloatInfo_tag:                    // 4
                in.skip(4);
                break;
            case LongInfo_tag:                     // 5
                in.skip(8);
                break;
            case DoubleInfo_tag:                   // 6
                in.skip(8);
                break;
            case ClassInfo_tag:                    // 7
                ClassInfo classInfo = new ClassInfo(in, mNumOfItems);
                mInfos[mNumOfItems] = classInfo;
//                println("  class", classInfo.index);
                break;
            case StringInfo_tag:                   // 8
                in.skip(2);
                break;
            case FieldrefInfo_tag:                 // 9
                in.skip(2 + 2);
                break;
            case MethodrefInfo_tag:                // 10
                in.skip(2 + 2);
                break;
            case InterfaceMethodrefInfo_tag:       // 11
                in.skip(2 + 2);
                break;
            case NameAndTypeInfo_tag:              // 12
                in.skip(2 + 2);
                break;
            case MethodHandleInfo_tag:             // 15
                in.skip(2 + 2);
                break;
            case MethodTypeInfo_tag:               // 16
                in.skip(2);
                break;
            case DynamicInfo_tag:                  // 17
                in.skip(2 + 2);
                break;
            case InvokeDynamicInfo_tag:            // 18
                in.skip(2 + 2);
                break;
            case ModuleInfo_tag:                   // 19
                in.skip(2);
                break;
            case PackageInfo_tag:                  // 20
                in.skip(2);
                break;
            default:
                String log = "invalid constant type: "
                        + tag + " at " + mNumOfItems + " path " + mPath;
//                throw new IOException(log);
                System.out.println(log);
        }

        mNumOfItems++;

        return tag;

    }

    interface Info {

    }

    class Utf8Info implements Info {
        byte[] data;
        short len;

        public Utf8Info(ByteReader in, int numOfItems) {
            len = in.readShort();
            data = in.reads(len);
        }
    }

    class ClassInfo implements Info {
        short index;

        public ClassInfo(ByteReader in, int numOfItems) {
            index = in.readShort();
        }
    }

}
