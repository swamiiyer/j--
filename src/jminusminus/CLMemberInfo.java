// Copyright 2012- Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This abstract base class represents the member_info structure.
 */
abstract class CLMemberInfo {
    /**
     * member_info.access_flags item.
     */
    public int accessFlags;

    /**
     * member_info.name_index item.
     */
    public int nameIndex;

    /**
     * member_info.descriptor_index item.
     */
    public int descriptorIndex;

    /**
     * member_info.attributes_count item.
     */
    public int attributesCount;

    /**
     * member_info.attributes item.
     */
    public ArrayList<CLAttributeInfo> attributes;

    /**
     * Constructs a CLMemberInfo object.
     *
     * @param accessFlags     member_info.access_flags item.
     * @param nameIndex       member_info.name_index item.
     * @param descriptorIndex member_info.descriptor_index item.
     * @param attributesCount member_info.attributes_count item.
     * @param attributes      member_info.attributes item.
     */
    protected CLMemberInfo(int accessFlags, int nameIndex, int descriptorIndex,
                           int attributesCount, ArrayList<CLAttributeInfo> attributes) {
        this.accessFlags = accessFlags;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
        this.attributesCount = attributesCount;
        this.attributes = attributes;
    }

    /**
     * Writes the contents of this class member to the specified output stream.
     *
     * @param out output stream.
     * @throws IOException if the contents of this class member can't be written to the specified
     *                     output stream.
     */
    public void write(CLOutputStream out) throws IOException {
        out.writeShort(accessFlags);
        out.writeShort(nameIndex);
        out.writeShort(descriptorIndex);
        out.writeShort(attributesCount);
        for (CLAttributeInfo attributeInfo : attributes) {
            attributeInfo.write(out);
        }
    }
}

/**
 * This class represents the field_info structure.
 */
class CLFieldInfo extends CLMemberInfo {
    /**
     * Constructs a CLFieldInfo object.
     *
     * @param accessFlags     field_info.access_flags item.
     * @param nameIndex       field_info.name_index item.
     * @param descriptorIndex field_info.descriptor_index item.
     * @param attributesCount field_info.attributes_count item.
     * @param attributes      field_info.attributes item.
     */
    public CLFieldInfo(int accessFlags, int nameIndex, int descriptorIndex, int attributesCount,
                       ArrayList<CLAttributeInfo> attributes) {
        super(accessFlags, nameIndex, descriptorIndex, attributesCount, attributes);
    }
}

/**
 * This class represents the method_info structure.
 */
class CLMethodInfo extends CLMemberInfo {
    /**
     * Constructs a CLMethodInfo object.
     *
     * @param accessFlags     method_info.access_flags item.
     * @param nameIndex       method_info.name_index item.
     * @param descriptorIndex method_info.descriptor_index item.
     * @param attributesCount method_info.attributes_count item.
     * @param attributes      method_info.attributes item.
     */

    public CLMethodInfo(int accessFlags, int nameIndex, int descriptorIndex, int attributesCount,
                        ArrayList<CLAttributeInfo> attributes) {
        super(accessFlags, nameIndex, descriptorIndex, attributesCount, attributes);
    }
}
