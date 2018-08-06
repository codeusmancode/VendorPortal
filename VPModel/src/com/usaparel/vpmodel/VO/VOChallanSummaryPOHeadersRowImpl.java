package com.usaparel.vpmodel.VO;

import java.math.BigDecimal;

import java.sql.Timestamp;

import oracle.jbo.domain.Number;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sat Nov 25 09:34:20 PKT 2017
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class VOChallanSummaryPOHeadersRowImpl extends ViewRowImpl {
    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. DO NOT MODIFY.
     */
    protected enum AttributesEnum {
        OrgId,
        OperatingUnit,
        PoHeaderId,
        PoNo,
        TypeLookupCode,
        CreationDate,
        ApprovedDate,
        SubmitDate,
        ClosedDate,
        VendorId,
        VendorName,
        VendorSiteId,
        VendorSiteCode,
        CurrencyCode,
        ChallanId,
        LineSum,
        VOChallanSummaryPOLines;
        private static AttributesEnum[] vals = null;
        ;
        private static final int firstIndex = 0;

        protected int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        protected static final int firstIndex() {
            return firstIndex;
        }

        protected static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        protected static final AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int ORGID = AttributesEnum.OrgId.index();
    public static final int OPERATINGUNIT = AttributesEnum.OperatingUnit.index();
    public static final int POHEADERID = AttributesEnum.PoHeaderId.index();
    public static final int PONO = AttributesEnum.PoNo.index();
    public static final int TYPELOOKUPCODE = AttributesEnum.TypeLookupCode.index();
    public static final int CREATIONDATE = AttributesEnum.CreationDate.index();
    public static final int APPROVEDDATE = AttributesEnum.ApprovedDate.index();
    public static final int SUBMITDATE = AttributesEnum.SubmitDate.index();
    public static final int CLOSEDDATE = AttributesEnum.ClosedDate.index();
    public static final int VENDORID = AttributesEnum.VendorId.index();
    public static final int VENDORNAME = AttributesEnum.VendorName.index();
    public static final int VENDORSITEID = AttributesEnum.VendorSiteId.index();
    public static final int VENDORSITECODE = AttributesEnum.VendorSiteCode.index();
    public static final int CURRENCYCODE = AttributesEnum.CurrencyCode.index();
    public static final int CHALLANID = AttributesEnum.ChallanId.index();
    public static final int LINESUM = AttributesEnum.LineSum.index();
    public static final int VOCHALLANSUMMARYPOLINES = AttributesEnum.VOChallanSummaryPOLines.index();

    /**
     * This is the default constructor (do not remove).
     */
    public VOChallanSummaryPOHeadersRowImpl() {
    }
}
