create or replace package body CURSPKG is

procedure sp_Page(p_PageSize       int, --每页记录数
                    p_PageNo         int, --当前页码,从 1 开始
                    p_SqlSelect      varchar2, --查询语句,含排序部分
                    p_SqlCount       varchar2, --获取记录总数的查询语句
                    p_OutRecordCount out int, --返回总记录数
                    test out varchar,
                    p_OutCursor      out refCursorType) is
    v_sql       varchar2(3000);
    v_count     int;
    v_heiRownum int;
    v_lowRownum int;
begin

    ----取记录总数
    execute immediate p_SqlCount
      into v_count;
    p_OutRecordCount := v_count;
    ----执行分页查询
    v_heiRownum := p_PageNo * p_PageSize;
    v_lowRownum := v_heiRownum - p_PageSize + 1;

    v_sql := 'SELECT * FROM (SELECT A.*, rownum rn
    FROM (' || p_SqlSelect || ') A
    WHERE rownum <= ' || to_char(v_heiRownum) || ') B
    WHERE rn >= ' || to_char(v_lowRownum);
    --注意对rownum别名的使用,第一次直接用rownum,第二次一定要用别名rn

    OPEN p_OutCursor FOR v_sql;

     EXCEPTION
    WHEN OTHERS THEN
      DBMS_output.PUT_LINE(SQLERRM);
      test:=SQLERRM;

end sp_Page;

--4.1客户信息校验接口
procedure YG001_Login(CustomerinId in varchar,
                      p_PageNo in int,
                      Rd1 in varchar,
                      Rd2 in varchar,
                      test out varchar,
                      p_OutCursor out refCursorType)is
    v_sql       varchar2(3000);
    v_count     int;

begin
    v_sql := 'select distinct nvl(a.fnumber,'''') CustomerId,nvl(a.fname_l2,'''') CustomerName,nvl(c.FContactPerson,'''') ContactName,nvl(c.FMobile,'''') CustomerTel from t_bd_customer a
left join t_bd_customersaleinfo b on a.fid = b.fcustomerid
left join T_BD_CustomerLinkMan c on b.fid = c.FCustomerSaleID
where a.fparentid is null  and  a.fnumber=''' || CustomerinId || '''' ;
    sp_page(p_pagesize => 100,
                  p_pageno => p_PageNo,
                  p_sqlselect => v_sql,
                  p_sqlcount => 'select 1 from dual ',
                  p_outrecordcount => v_count,
                  test => test,
                  p_outcursor => p_OutCursor);
     test := v_sql;

      EXCEPTION
    WHEN OTHERS THEN
      DBMS_output.PUT_LINE(SQLERRM);
      test:=SQLERRM;

end YG001_Login;

procedure YG002_GetSecCustInfo(CustomerinId in varchar,
                      p_PageNo in int,
                      Rd1 in varchar,
                      Rd2 in varchar,
                      test out varchar,
                      p_OutCursor out refCursorType) is
    v_sql       varchar2(3000);
    v_count     int;

begin
 v_sql := 'select distinct nvl(a.fnumber,'''') CustomerId,0 CustomerDebt ,
    nvl(sec.fnumber,a.fnumber) SecCustomerId,nvl(sec.fname_l2,a.fname_l2) SecCustomerName,
    nvl(c.FContactPerson,'''') SecContactName,nvl(c.FMobile,'''') SecCustomerTel ,
     0 SecCustomerDebt ,nvl(e.fname_l2,'''') Province,nvl(f.fname_l2,'''') Area
from t_bd_customer a
left join t_bd_customer sec on sec.fparentid = a.fid
left join t_bd_customersaleinfo b on sec.fid = b.fcustomerid
left join T_BD_CustomerLinkMan c on b.fid = c.FCustomerSaleID
left join T_BD_Province e on sec.FProvince = e.fid
left join T_BD_Region f on sec.FRegionID = f.fid
where a.fcontrolunitid in (''00000000-0000-0000-0000-000000000000CCE7AED4'',''sREAAAAAB9LM567U'') 
and (b.fcontrolunitid =''sREAAAAAB9LM567U'' or b.fcontrolunitid is null ) 
 and a.fnumber=''' || CustomerinId || '''' ;
    sp_page(p_pagesize => 100,
                  p_pageno => p_PageNo,
                  p_sqlselect => v_sql,
                  p_sqlcount => 'select 1 from dual ',
                  p_outrecordcount => v_count,
                  test => test,
                  p_outcursor => p_OutCursor);
     test := v_sql;

      EXCEPTION
    WHEN OTHERS THEN
      DBMS_output.PUT_LINE(SQLERRM);
      test:=SQLERRM;

end YG002_GetSecCustInfo;

procedure YG003_GetCustInfo(p_PageNo in int,
                      Rd1 in varchar,
                      Rd2 in varchar,
                      test out varchar,
                      p_OutCursor out refCursorType) is
    v_sql       varchar2(6000);
    v_count     int;

begin
    v_sql := 'select distinct nvl(a.fnumber,'''') CustomerId,nvl(a.fname_l2,'''') CustomerName ,
       nvl(e.fname_l2,'''') Province,nvl(f.fname_l2,'''') Area ,
    nvl(sec.fnumber,'''') ParentId,nvl(sec.fname_l2,'''') ParentName,
    nvl(c.FContactPerson,'''') ContactName,nvl(c.FMobile,'''') CustomerTel,
    nvl(e.fname_l2,'''') fProvince,nvl(f.fname_l2,'''') fArea
from t_bd_customer a
left join t_bd_customer sec on a.fparentid = sec.fid
left join t_bd_customersaleinfo b on a.fid = b.fcustomerid
left join T_BD_CustomerLinkMan c on b.fid = c.FCustomerSaleID
left join T_BD_Province e on a.FProvince = e.fid
left join T_BD_Region f on a.FRegionID = f.fid
where a.fcontrolunitid in (''00000000-0000-0000-0000-000000000000CCE7AED4'',''sREAAAAAB9LM567U'') and b.fcontrolunitid in (''00000000-0000-0000-0000-000000000000CCE7AED4'',''sREAAAAAB9LM567U'')  ' ;
      sp_page(p_pagesize => 100,
                  p_pageno => p_PageNo,
                  p_sqlselect => v_sql,
                  p_sqlcount => 'select 1 from dual ',
                  p_outrecordcount => v_count,
                  test => test,
                  p_outcursor => p_OutCursor);
     test := v_sql;

      EXCEPTION
    WHEN OTHERS THEN
      DBMS_output.PUT_LINE(SQLERRM);
      test:=SQLERRM;

end YG003_GetCustInfo;


procedure YG101_GetProductInfo(p_PageNo in int,
                      Rd1 in varchar,
                      Rd2 in varchar,
                      test out varchar,
                      p_OutCursor out refCursorType) is
    v_sql       varchar2(3000);
    v_count     int;

begin
    v_sql := 'select nvl(a.fnumber,'''') ProductId,nvl(a.fname_l2,'''') ProductName,nvl(FModel,'''') Specification,a.falias falias,
nvl(unit1.fname_l2,'''') Unit,unit2.fname_l2 baseUnit,a.FHelpCode StandardLen ,nvl(mgroup.fnumber,'''') CategoryId,
nvl(a.fname_l2,'''') Brand
 from t_bd_material a
 left join T_BD_MeasureUnit unit1 on a.FAssistUnit = unit1.fid
 left join T_BD_MeasureUnit unit2 on a.FBaseUnit = unit2.fid
 left join T_BD_MaterialGroup mgroup on a.FMaterialGroupID = mgroup.fid
inner join T_BD_MaterialGroupStandard c on mgroup.FGroupStandard = c.fid and c.FStandardType=1
inner join T_BD_MaterialSales d on d.FMaterialID = a.fid
left join T_BD_DataBaseDAssign e on e.FDataBaseDID = a.fid
where a.FStatus = 1 and a.fcontrolunitid in (''00000000-0000-0000-0000-000000000000CCE7AED4'',''sREAAAAAB9LM567U'')
and  d.FOrgUnit = ''sREAAAAAB9LM567U''
and e.FAssignCUID = ''sREAAAAAB9LM567U''
and d.fstatus = 1
and (mgroup.fnumber like ''101%'' or mgroup.fnumber like ''102%'' or mgroup.fnumber like ''103%'' or mgroup.fnumber like ''109%''
or mgroup.fnumber like ''110%''  or mgroup.fnumber like ''111%'' or mgroup.fnumber like ''117%'' or mgroup.fnumber like ''20101%''
 or mgroup.fnumber like ''30101%'') 
and a.fname_l2 not like ''%胶水%'' 
order by a.fname_l2,a.fnumber ';
    sp_page(p_pagesize => 100,
                  p_pageno => p_PageNo,
                  p_sqlselect => v_sql,
                  p_sqlcount => 'select 1 from dual ',
                  p_outrecordcount => v_count,
                  test => test,
                  p_outcursor => p_OutCursor);
     test := v_sql;

      EXCEPTION
    WHEN OTHERS THEN
      DBMS_output.PUT_LINE(SQLERRM);
      test:=SQLERRM;

end YG101_GetProductInfo;

---4.5企业产品分类日终同步接口D00100
procedure YG100_GetCategoryInfo(p_PageNo in int,
                      Rd1 in varchar,
                      Rd2 in varchar,
                      test out varchar,
                      p_OutCursor out refCursorType) is
    v_sql       varchar2(3000);
    v_count     int;

begin
    v_sql := 'select distinct nvl(a.fnumber,'''') CategoryId,nvl(a.fname_l2,'''') CategoryName,nvl(a.FLevel,'''') fLevel, case when b.fid is null then 1 else 0 end  HasChild
from T_BD_MaterialGroup a
inner join  T_BD_MaterialGroup b on b.FParentID = a.fid and b.FDeletedStatus =1
inner join T_BD_MaterialGroupStandard c on a.FGroupStandard = c.fid and c.FStandardType=1
where a.FDeletedStatus =1  and a.fcontrolunitid in (''00000000-0000-0000-0000-000000000000CCE7AED4'',''sREAAAAAB9LM567U'')
and (a.fnumber like ''101%'' or a.fnumber like ''102%'' or a.fnumber like ''103%'' or a.fnumber like ''109%''
or a.fnumber like ''110%''  or a.fnumber like ''111%'' or a.fnumber like ''117%'' or a.fnumber like ''20101%''
 or a.fnumber like ''30101%'') ' ;
    sp_page(p_pagesize => 100,
                  p_pageno => p_PageNo,
                  p_sqlselect => v_sql,
                  p_sqlcount => 'select 1 from dual ',
                  p_outrecordcount => v_count,
                  test => test,
                  p_outcursor => p_OutCursor);
     test := v_sql;

      EXCEPTION
    WHEN OTHERS THEN
      DBMS_output.PUT_LINE(SQLERRM);
      test:=SQLERRM;

end YG100_GetCategoryInfo;

--工行提交订单接口D00301
/*procedure YG301_InsertOnlineOrder_old(OrderId  in  varchar,
                       CustomerId  in  varchar,
                       SecCustomerId  in  varchar,
                       SecCustomerName  in  varchar,
                       CreateTime  in  varchar,
                       Address  in  varchar,
                       ShipMethod  in  varchar,
                       Contact  in  varchar,
                       ContactNum  in  varchar,
                       ToDate  in  varchar,
                       Remark  in  varchar,
                       Rd1  in  varchar,
                       Rd2  in  varchar,
                       OrderEntry  in  varchar,
                       ProductId  in  varchar,
                       ProductName  in  varchar,
                       Specification  in  varchar,
                       Unit  in  varchar,
                       StandardLen  in  varchar,
                       icbcCount  in  varchar,
                       res out int)is
    v_sql       varchar2(3000);
    v_count     int;

begin
    v_sql := 'select 1 from dual ' ;

  \*  IF OrderEntry=1 THEN
             ;
      ELSE
      ;
     END IF;*\


end YG301_InsertOnlineOrder_old;   */


procedure YG302_GetOrderInfo(OrderId in varchar,
                      Rd1 in varchar,
                      Rd2 in varchar,
                      test out varchar,
                      p_OutCursor out refCursorType) is
    v_sql       varchar2(3000);
    v_count     int;

begin
    v_sql := 'select a.cforderid OrderId,a.cfbillstate billstatus,
  c.fnumber OrderCode,
  c.FBaseStatus OrderStatus,
  c.FCreateTime CreateTime,
  c.FBizDate BusinessDate,
h.fnumber SecCustomerId,
h.fname_l2 SecCustomerName,
i.fnumber CompanyId,
i.fname_l2 CompanyName,
c.FTotalAmount SumTotal,
c.FLinkMan Contact,
c.FCustomerPhone ContactNum,
c.fdescription Remark,
f.fnumber DeliveryOrderId,
d.fseq OrderEntry,
j.fnumber ProductId,
j.fname_l2 ProductName,
j.FModel Specification,
k.fname_l2 Unit,
j.FHelpCode StandardLen,
d.FQty fCount,
d.FPlanDeliveryQty ArrangedQty,
d.FTotalIssueBaseQty OutedQty
from CT_CUS_ICBC_SALEORDER a
left outer join T_BOT_Relation b on a.fid = b.fsrcobjectid
left outer join  T_SD_SaleOrder c on b.fdestobjectid = c.fid
left outer join t_sd_saleorderentry d on c.fid = d.fparentid
left outer join T_BOT_Relation e on c.fid = e.fsrcobjectid
left outer join T_DT_CarryBill f on e.fdestobjectid = f.fid
left outer join T_BD_Customer h on c.FOrderCustomerID = h.fid
left outer join T_ORG_Sale i on c.fsaleorgunitid = i.fid
left outer join t_bd_material j on j.fid = d.fmaterialid
left outer join T_BD_MeasureUnit k on j.FAssistUnit = k.fid
left outer join T_BD_MeasureUnit l on j.FBaseUnit = l.fid
where a.cforderid=''' || OrderId || ''' order by c.fnumber,d.fseq ' ;
    sp_page(p_pagesize => 10000,
                  p_pageno => 1,
                  p_sqlselect => v_sql,
                  p_sqlcount => 'select 1 from dual ',
                  p_outrecordcount => v_count,
                  test => test,
                  p_outcursor => p_OutCursor);
     test := v_sql;

     EXCEPTION
    WHEN OTHERS THEN
      DBMS_output.PUT_LINE(SQLERRM);
      test:=SQLERRM;

end YG302_GetOrderInfo;


procedure YG404_InsertPayInfo(SecCustomerId  in  varchar,
                      SecCustomerName  in  varchar,
                      PaymentId  in  varchar,
                      PaymentDate  in  varchar,
                      PayMethod  in  varchar,
                      PayAmount  in  varchar,
                      Bank  in  varchar,
                      AccountNo  in  varchar,
                      Remark  in  varchar,
                      Rd1  in  varchar,
                      Rd2  in  varchar,
                      icbcSecCustomerId out varchar,
                      icbcPaymentId out varchar)is
    v_sql       varchar2(3000);
    v_count     int;
    exist       int;

begin

select count(fid) into exist from CT_CUS_Icbc_payment where cfpaymentid = paymentid ;

if exist is not null and exist > 0 then
   icbcSecCustomerId := 'error.billno exist';
else
    insert into CT_CUS_Icbc_payment(fid,CFSecCustomerId,cfPaymentId,
cfPaymentDate,cfPayMethod,cfPayAmount,cfBank,cfAccountNo,cfRemark,cfRd1,cfRd2,
FCREATORID,FCREATETIME,FLASTUPDATEUSERID,FLASTUPDATETIME,FCONTROLUNITID)
values(newbosid('B33B6367'),
''||SecCustomerId||'',
''||PaymentId||'',
''||PaymentDate||'',
''||PayMethod||'',
''||PayAmount||'',
''||Bank||'',
''||AccountNo||'',
''||Remark||'',
''||Rd1||'',
''||Rd2||'',
'256c221a-0106-1000-e000-10d7c0a813f413B7DE7F',
sysdate,'256c221a-0106-1000-e000-10d7c0a813f413B7DE7F',
sysdate,'00000000-0000-0000-0000-000000000000CCE7AED4');

commit;
icbcSecCustomerId := SecCustomerId;
icbcPaymentId := PaymentId;
end if;

Exception

When others then

Dbms_output.put_line(sqlcode||sqlerrm(sqlcode));
icbcPaymentId := SQLERRM;


end YG404_InsertPayInfo;



procedure YG301_InsertOnlineOrder(partPopedom_xml in clob,test out varchar,res out int)
as
        OrderId  varchar2(100);
        CustomerId  varchar2(100);
        SecCustomerId  varchar2(100);
        SecCustomerName  varchar2(300);
        CreateTime  varchar2(100);
        Address  varchar2(1024);
        ShipMethod  varchar2(100);
        Contact  varchar2(100);
        ContactNum  varchar2(100);
        ToDate  varchar2(100);
        Remark  varchar2(500);
        Rd1  varchar2(100);
        Rd2  varchar2(100);

        OrderEntry  varchar2(100);
        ProductId  varchar2(100);
        ProductName  varchar2(500);
        Specification  varchar2(100);
        Unit  varchar2(100);
        StandardLen  varchar2(100);
        icbcCount  varchar2(100);
        materialid varchar2(100);
        PARENTID varchar2(100);
        exist int;
        fcustmoerid varchar2(100);

--XML解析器
 xmlPar XMLPARSER.parser := XMLPARSER.NEWPARSER;
 --//DOM文档对象
 doc xmldom.DOMDocument;
 len Integer;
 personNodes xmldom.DOMNodeList;
 chilNodes xmldom.DOMNodeList;
 tempNode xmldom.DOMNode;
 tempArrMap xmldom.DOMNamedNodeMap;
 --================================
 --以下变量用于获取XML节点的值
/* partNum varchar2(50); --角色编号
 menuNum varchar2(50); --菜单编号
 operateNum varchar2(50); --操作编号*/
 tmp Integer;
rootnode    dbms_xmldom.domnode;
--================
BEGIN
 xmlPar := xmlparser.newParser;
 xmlparser.parseClob(xmlPar,partPopedom_xml);
 doc := xmlparser.getDocument(xmlPar);
 -- 释放解析器实例
 xmlparser.freeParser(xmlPar);

 personNodes := xmldom.getElementsByTagName(doc, 'ProductInfo');
 len := xmldom.getLength( personNodes );


  SELECT EXTRACTVALUE(VALUE(t),'/in/OrderId'),
         EXTRACTVALUE(VALUE(t),'/in/CustomerId'),
         EXTRACTVALUE(VALUE(t),'/in/SecCustomerId'),
         EXTRACTVALUE(VALUE(t),'/in/SecCustomerName'),
         EXTRACTVALUE(VALUE(t),'/in/CreateTime'),
         EXTRACTVALUE(VALUE(t),'/in/Address'),
         EXTRACTVALUE(VALUE(t),'/in/ShipMethod'),
         EXTRACTVALUE(VALUE(t),'/in/Contact'),
         EXTRACTVALUE(VALUE(t),'/in/ContactNum'),
         EXTRACTVALUE(VALUE(t),'/in/ToDate'),
         EXTRACTVALUE(VALUE(t),'/in/Remark'),
         EXTRACTVALUE(VALUE(t),'/in/Rd1'),
         EXTRACTVALUE(VALUE(t),'/in/Rd2')
           INTO OrderId,
           CustomerId,
           SecCustomerId,
           SecCustomerName,
           CreateTime,
           Address,
           ShipMethod,
           Contact,
           ContactNum,
           ToDate,
           Remark,
           Rd1,
           Rd2
    FROM TABLE(XMLSEQUENCE(EXTRACT(XMLTYPE(partPopedom_xml), '/in'))) t;

 select count(fid) into exist from CT_CUS_ICBC_SALEORDER where CFORDERID = OrderId ;

select fid into fcustmoerid from T_BD_Customer where fnumber=SecCustomerId and fcontrolunitid in ('00000000-0000-0000-0000-000000000000CCE7AED4','sREAAAAAB9LM567U');

if exist is not null and exist > 0 then
   test := 'error.billno exist';
else

 PARENTID := newbosid('D1F43888');
 --insert maintable data
insert into CT_CUS_ICBC_SALEORDER(fid,CFORDERID,CFICBCCUSTOMERID,
CFSECCUSTOMERID,CFICBCCREATETIME,CFADDRESS,CFSHIPMETHOD,CFCONTACT,
CFCONTACTNUM,CFTODATE,CFREMARK,CFRD1,CFRD2,FBIZDATE,CFREQDATE,
CFCUSTOMERID,
FCREATORID,FCREATETIME,FLASTUPDATEUSERID,FLASTUPDATETIME,FCONTROLUNITID)
values(PARENTID,
''||OrderId||'',
''||CustomerId||'',
''||SecCustomerId||'',
''||CreateTime||'',
''||Address||'',
''||ShipMethod||'',
''||Contact||'',
''||ContactNum||'',
''||ToDate||'',
''||Remark||'',
''||Rd1||'',
''||Rd2||'',
sysdate,
to_timestamp(ToDate,'''yyyyMMdd'''),
''||fcustmoerid||'',
'256c221a-0106-1000-e000-10d7c0a813f413B7DE7F',
sysdate,'256c221a-0106-1000-e000-10d7c0a813f413B7DE7F',
sysdate,'00000000-0000-0000-0000-000000000000CCE7AED4');

 --遍历所有PERSON元素
 FOR i in 0..len-1
   LOOP
     --获取第i个
     tempNode := xmldom.item(personNodes, i);
     --所有属性
     tempArrMap := xmldom.getAttributes(tempNode);
     --获取PERSONID的值
     --pid := xmldom.getNodeValue(xmldom.getNamedItem(tempArrMap,'POPEDOM'));
     --获取子元素的值
     chilNodes := xmldom.getChildNodes(tempNode);
     tmp := xmldom.GETLENGTH(chilNodes);
     OrderEntry := xmldom.getNodeValue(xmldom.getFirstChild(xmldom.item(chilNodes, 0)));
     ProductId := xmldom.getNodeValue(xmldom.getFirstChild(xmldom.item(chilNodes, 1)));
     ProductName := xmldom.getNodeValue(xmldom.getFirstChild(xmldom.item(chilNodes, 2)));
     Specification := xmldom.getNodeValue(xmldom.getFirstChild(xmldom.item(chilNodes, 3)));
     Unit := xmldom.getNodeValue(xmldom.getFirstChild(xmldom.item(chilNodes, 4)));
     StandardLen := xmldom.getNodeValue(xmldom.getFirstChild(xmldom.item(chilNodes, 5)));
     icbcCount := xmldom.getNodeValue(xmldom.getFirstChild(xmldom.item(chilNodes, 6)));
     --translate number to id
     select fid into materialid from t_bd_material where fnumber=ProductId and fcontrolunitid in ('00000000-0000-0000-0000-000000000000CCE7AED4','sREAAAAAB9LM567U');
     --插入数据
     insert into CT_CUS_ICBC_SALEORDERENTRY(fid,FPARENTID,FSEQ,CFORDERENTRY,CFPRODUCTID,CFSTANDARDLEN,CFUNIT,CFCOUNT,Cfnum,CFMATERIALID)
            values(newbosid('C201F4CA'),
                 ''||PARENTID||'',
                 i+1,
                 ''||OrderEntry||'',
                 ''||ProductId||'',
                 ''||StandardLen||'',
                 ''||Unit||'',
                 icbcCount,
                 icbcCount,
                 ''||materialid||'');

END LOOP;

    COMMIT;

    end if ;
   -- 释放文档对象
   xmldom.freeDocument(doc);
   EXCEPTION
    WHEN OTHERS THEN
      DBMS_output.PUT_LINE(SQLERRM);
      test:=SQLERRM;
 END YG301_InsertOnlineOrder;

procedure YG501_GetCustomerBalance(customerid in varchar,
                      startdate in varchar,
                      enddate in varchar,
                      test out clob,
                       p_OutCursor out refCursorType) is
    v_sql       clob;
    v_count     int;

begin
    v_sql := 'select fcussnum,fcussname,sum(FStartBalanceAmt) FStartBalanceAmt, sum(FDebitAmt) FDebitAmt,  sum(FCreditAmt) FCreditAmt,  sum(FBalanceAmt) FBalanceAmt
from (
  select  t4.FName_l2 FCurrency,  t2.FId FCussTypeId, t2.FName_l2 FCussType
, t3.FNumber FCussNum, t3.FName_l2 FCussName, t3.FId FCussAcctId, t1.FCurrencyId FCurrencyId
,  sum(t1.FBeginBalanceFor) FStartBalanceAmt, 0 FDebitAmt, 0 FCreditAmt, 0 FBalanceAmt
  from t_ar_arBalance t1 left join t_bd_AsstActType t2 on t2.FId = t1.FAsstActTypeId
 inner join t_bd_customer t3 on t3.FId = t1.FAsstActId left join t_bd_currency t4 on t4.FId = t1.FCurrencyId
 where 1 = 1 and FBalType IN (70,100) and (t1.FAsstActTypeId = ''YW3xsAEJEADgAAUWwKgTB0c4VZA='')  and t1.FCompanyid = ''sREAAAAAB9LM567U''
 and t1.FPeriodId = ''sREAAAAmbTOCOIxM''
 group by   t2.FId, t2.FName_l2 , t3.FNumber, t3.FName_l2 , t1.FAsstActId, t4.FName_l2 , t1.FCurrencyId, t3.FId
 union all
 select  t4.FName_l2 FCurrency,  t2.FId FCussTypeId, t2.FName_l2 FCussType
, t3.FNumber FCussNum, t3.FName_l2 FCussName, t3.FId FCussAcctId, t1.FCurrencyId FCurrencyId
, -1 * sum(t1.FBeginBalanceFor) FStartBalanceAmt, 0 FDebitAmt, 0 FCreditAmt, 0 FBalanceAmt
  from t_ap_apBalance t1 left join t_bd_AsstActType t2 on t2.FId =  t1.FAsstActTypeId
 inner join t_bd_customer t3 on t3.FId = t1.FAsstActId left join t_bd_currency t4 on t4.FId = t1.FCurrencyId
 where 1 = 1  and FBalType IN (70,100) and (t1.FAsstActTypeId = ''YW3xsAEJEADgAAUWwKgTB0c4VZA='')  and t1.FCompanyid = ''sREAAAAAB9LM567U''
 and t1.FPeriodId = ''sREAAAAmbTOCOIxM''
 group by   t2.FId, t2.FName_l2 , t3.FNumber, t3.FName_l2 , t1.FAsstActId, t4.FName_l2 , t1.FCurrencyId, t3.FId
 union all
  select  t4.FName_l2 FCurrency,  t2.FId FCussTypeId, t2.FName_l2 FCussType
, t3.FNumber FCussNum, t3.FName_l2 FCussName, t3.FId FCussAcctId, t1.FCurrencyId FCurrencyId
,  sum(t1.FAmounts) FStartBalanceAmt, 0 FDebitAmt, 0 FCreditAmt, 0 FBalanceAmt
  from T_AR_InitBadAccount t1 left join t_bd_AsstActType t2 on t2.FId = t1.FAsstActTypeID
 left join t_bd_customer t3 on t3.FId = t1.FAcctCussentID
 left join t_bd_currency t4 on t4.FId = t1.FCurrencyId
 where 1= 1 and t1.FAcctCussentID in (select FId from t_bd_customer)
 and (t1.FAsstActTypeID = ''YW3xsAEJEADgAAUWwKgTB0c4VZA='')  and t1.FCompanyid = ''sREAAAAAB9LM567U''
 group by   t2.FId, t2.FName_l2 , t3.FNumber, t3.FName_l2 , t3.FId, t4.FName_l2 , t1.FCurrencyId

 union all

 select t4.FName_l2 FCurrency,  t2.FId FCussTypeId
, t2.FName_l2 FCussType, t3.FNumber FCussNum, t3.FName_l2 FCussName, t3.FId FCussAcctId
, t4.FId FCurrencyId, 0 FStartBalanceAmt, sum(t1.FReceiving) FDebitAmt, sum(t1.FActual) FCreditAmt, 0 FBalanceAmt
 from
 (select t1.FCompanyId FCompanyId, t1.FAsstActID FCUSTOMERID, t1.FCurrencyId FCURRENCYID, t1.FBillDate FBILLDATE
,  t1.FAmount FRECEIVING, 0 FActual , t1.FAuditorID FAUDITORID, t1.FAsstActTypeID FASSTACTTYPEID
 from T_AR_OTHERBILL t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAsstActID
 where 1= 1 and t1.FBillStatus = 3 AND t1.FIsBizBill = 0 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBillDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBillDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FPayerID FCUSTOMERID, t1.FCurrencyID FCURRENCYID, t1.FBizDate FBILLDATE
,  0 FRECEIVING, t1.FAmount FActual , t1.FAuditorID FAUDITORID, t1.FPayerTypeID FASSTACTTYPEID
 from T_CAS_ReceivingBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FPayerID
 where 1= 1 and t1.FBillStatus >= 14 and t1.FSourceType = 100 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FAcctCussID_Main FCUSTOMERID, t1.FCurrencyID_Main FCURRENCYID, t1.FBizDate FBILLDATE
,  0 FRECEIVING, t1.FThisVerificateAmt_Main FActual , ''1'' FAUDITORID, t1.FAcctCussTypeId_Main FASSTACTTYPEID
 from T_AR_VerificationBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAcctCussID_Main
 where 1= 1 and ((t1.FVerificationType = 101 or t1.FVerificationType = 107 or t1.FVerificationType = 103 or t1.FVerificationType = 109) and ((t1.FIsSameCode = 1 AND t1.FIsSameCurrency = 0) or t1.FIsSameCode = 0))
 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FAcctCussID_Second FCUSTOMERID, t1.FCurrencyID_Second FCURRENCYID, t1.FBizDate FBILLDATE
,  t1.FThisVerificateAmt_Second FRECEIVING, 0 FActual , ''1'' FAUDITORID, t1.FAcctCussTypeId_Second FASSTACTTYPEID
 from T_AR_VerificationBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAcctCussID_Second
 where 1= 1 and ((t1.FVerificationType = 101 or t1.FVerificationType = 107 ) and ((t1.FIsSameCode = 1 AND t1.FIsSameCurrency = 0) or t1.FIsSameCode = 0))
 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FAcctCussID_Main FCUSTOMERID, t1.FCurrencyID_Main FCURRENCYID, t1.FBizDate FBILLDATE
, t1.FThisVerificateAmt_Main FRECEIVING, 0 FActual , ''1'' FAUDITORID, t1.FAcctCussTypeId_Main FASSTACTTYPEID
 from T_AR_VerificationBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAcctCussID_Main
 where 1= 1 and ((t1.FVerificationType = 104 or t1.FVerificationType = 110 or t1.FVerificationType = 113 or t1.FVerificationType = 114) and ((t1.FIsSameCode = 1 AND t1.FIsSameCurrency = 0) or t1.FIsSameCode = 0))
 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FAcctCussID_Second FCUSTOMERID, t1.FCurrencyID_Second FCURRENCYID, t1.FBizDate FBILLDATE
,  0 FRECEIVING, t1.FThisVerificateAmt_Second  FActual , ''1'' FAUDITORID, t1.FAcctCussTypeId_Second FASSTACTTYPEID
 from T_AP_VerificationBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAcctCussID_Second
 where 1= 1 and ((t1.FVerificationType = 203 or t1.FVerificationType = 209 ) and ((t1.FIsSameCode = 1 AND t1.FIsSameCurrency = 0) or t1.FIsSameCode = 0))
 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FAcctCussID_Second FCUSTOMERID, t1.FCurrencyID_Second FCURRENCYID, t1.FBizDate FBILLDATE
,  t1.FThisVerificateAmt_Second FRECEIVING, 0 FActual , ''1'' FAUDITORID, t1.FAcctCussTypeId_Second FASSTACTTYPEID
 from T_AP_VerificationBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAcctCussID_Second
 where 1= 1 and ((t1.FVerificationType = 204 or t1.FVerificationType = 210 or t1.FVerificationType = 211 or t1.FVerificationType = 212) and ((t1.FIsSameCode = 1 AND t1.FIsSameCurrency = 0) or t1.FIsSameCode = 0))
 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select A.FCompanyId FCompanyId, B.FAsstActId FCUSTOMERID, A.FCurrencyId FCURRENCYID, A.FBillDate FBILLDATE,
  0 FRECEIVING, A.FAmount FActual , A.FAuditorID FAUDITORID, B.FAsstActTypeId FASSTACTTYPEID
 from T_AR_OTHERBILL A INNER JOIN T_AR_OTHERBILL B ON A.FSourceBillID = B.FID and A.FCompanyId = B.FCompanyId
 inner join t_bd_customer tCus on tCus.FId = B.FAsstActId
 where 1=1  and A.FCompanyId = ''sREAAAAAB9LM567U''

 and A.FBillDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'') and A.FBillDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 and A.FCurrencyId = B.FCurrencyId  and B.FCompanyId = ''sREAAAAAB9LM567U'' and B.FBillDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
  and B.FBillStatus = 3 and (A.FIsReverseBill = 0  and A.FIsTransBill = 1 and A.FBillStatus = 3 )
 union all
select A.FCompanyId FCompanyId, B.FPayerID FCUSTOMERID, A.FCurrencyId FCURRENCYID,  A.FBizDate  FBILLDATE,
 A.FAmount FRECEIVING, 0 FActual , A.FAuditorID FAUDITORID, B.FPayerTypeID FASSTACTTYPEID
 from T_CAS_ReceivingBill A INNER JOIN T_CAS_ReceivingBill B ON A.FSourceBillID = B.FID and A.FCompanyId = B.FCompanyId
 inner join t_bd_customer tCus on tCus.FId = B.FPayerID
 where 1=1  and A.FCompanyId = ''sREAAAAAB9LM567U''

 and  A.FBizDate  >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'') and  A.FBizDate  < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 and A.FCurrencyId = B.FCurrencyId  and B.FCompanyId = ''sREAAAAAB9LM567U'' and  B.FBizDate  < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
  and B.FSourceType= 100 and B.FBillStatus>= 14  AND A.FSourceType= 100 and A.FBillStatus>= 14 and A.FIsTransBill = 1
 union all
select A.FCompanyId FCompanyId, B.FAsstActID FCUSTOMERID, A.FCurrencyId FCURRENCYID,  A.FBizDate  FBILLDATE,
 0 AS FRECEIVING, A.FAmount As FActual , A.FAuditorID FAUDITORID, B.FAsstActTypeID FASSTACTTYPEID
 from T_CAS_PaymentBill A INNER JOIN T_AR_OtherBill B ON A.FSourceBillID = B.FID and A.FCompanyId = B.FCompanyId
 inner join t_bd_customer tCus on tCus.FId = B.FAsstActID
 where 1=1  and A.FCompanyId = ''sREAAAAAB9LM567U''

 and  A.FBizDate  >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'') and  A.FBizDate  < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 and A.FCurrencyId = B.FCurrencyId  and B.FCompanyId = ''sREAAAAAB9LM567U'' and  B.FBillDate  < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
  and A.FSourceType= 101 and A.FBillStatus>= 15 AND B.FBillStatus= 3 and A.FIsTransOtherBill = 1
 )  t1 left join t_bd_AsstActType t2 on t2.FId = t1.FAsstActTypeId
 left join t_bd_customer t3 on t3.FId = t1.FCustomerId left join t_bd_currency t4 on t4.FId = t1.FCurrencyId
 group by   t2.FId, t2.FName_l2 , t3.FNumber, t3.FName_l2 , t3.FId, t4.FName_l2 , t4.FId
 union all
 select t4.FName_l2 FCurrency,  t2.FId FCussTypeId
, t2.FName_l2 FCussType, t3.FNumber FCussNum, t3.FName_l2 FCussName, t3.FId FCussAcctId
, t4.FId FCurrencyId, 0 FStartBalanceAmt, sum(t1.FActual) FDebitAmt, sum(t1.FPayment) FCreditAmt, 0 FBalanceAmt
 from
 (select t1.FCompanyId FCompanyId, t1.FAsstActID FSUPPLIERID, t1.FCurrencyID FCURRENCYID, t1.FBillDate FBILLDATE
,  t1.FAmount FPayment, 0 FActual , t1.FAuditorID FAUDITORID, t1.FAsstActTypeID FASSTACTTYPEID
 from T_AP_OTHERBILL t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAsstActID
 where 1= 1 and t1.FBillStatus = 3 AND t1.FIsBizBill = 0 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBillDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBillDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FPayeeID FSUPPLIERID, t1.FCurrencyID FCURRENCYID, t1.FBizDate FBILLDATE
,  0 FPAYMENT, t1.FAmount FActual , t1.FAuditorID FAUDITORID, t1.FPayeeTypeID FASSTACTTYPEID
 from T_CAS_PaymentBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FPayeeID
 where 1= 1 and t1.FBillStatus >= 15 and t1.FSourceType = 101 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FAcctCussID_Main FSUPPLIERID, t1.FCurrencyID_Main FCURRENCYID, t1.FBizDate FBILLDATE
,  0 FPayment, t1.FThisVerificateAmt_Main FActual , ''1'' FAUDITORID, t1.FAcctCussTypeId_Main FASSTACTTYPEID
 from T_AP_VerificationBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAcctCussID_Main
 where 1= 1 and ((t1.FVerificationType = 201 or t1.FVerificationType = 207 or t1.FVerificationType = 203 or t1.FVerificationType = 209) and ((t1.FIsSameCode = 1 AND t1.FIsSameCurrency = 0) or t1.FIsSameCode = 0))
 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FAcctCussID_Second FSUPPLIERID, t1.FCurrencyID_Second FCURRENCYID, t1.FBizDate FBILLDATE
,  t1.FThisVerificateAmt_Second FPayment, 0 FActual , ''1'' FAUDITORID, t1.FAcctCussTypeId_Second FASSTACTTYPEID
 from T_AP_VerificationBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAcctCussID_Second
 where 1= 1 and ((t1.FVerificationType = 201 or t1.FVerificationType = 207 ) and ((t1.FIsSameCode = 1 AND t1.FIsSameCurrency = 0) or t1.FIsSameCode = 0))
 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FAcctCussID_Main FSUPPLIERID, t1.FCurrencyID_Main FCURRENCYID, t1.FBizDate FBILLDATE
,  t1.FThisVerificateAmt_Main FPayment, 0 FActual , ''1'' FAUDITORID, t1.FAcctCussTypeId_Main FASSTACTTYPEID
 from T_AP_VerificationBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAcctCussID_Main
 where 1= 1 and ((t1.FVerificationType = 204 or t1.FVerificationType = 210 or t1.FVerificationType = 211 or t1.FVerificationType = 212) and ((t1.FIsSameCode = 1 AND t1.FIsSameCurrency = 0) or t1.FIsSameCode = 0))
 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FAcctCussID_Second FSUPPLIERID, t1.FCurrencyID_Second FCURRENCYID, t1.FBizDate FBILLDATE
,  0 FPayment, t1.FThisVerificateAmt_Second FActual , ''1'' FAUDITORID, t1.FAcctCussTypeId_Second FASSTACTTYPEID
 from T_AR_VerificationBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAcctCussID_Second
 where 1= 1 and ((t1.FVerificationType = 103 or t1.FVerificationType = 109 ) and ((t1.FIsSameCode = 1 AND t1.FIsSameCurrency = 0) or t1.FIsSameCode = 0))
 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select t1.FCompanyId FCompanyId, t1.FAcctCussID_Second FSUPPLIERID, t1.FCurrencyID_Second FCURRENCYID, t1.FBizDate FBILLDATE
,  t1.FThisVerificateAmt_Second FPayment, 0 FActual , ''1'' FAUDITORID, t1.FAcctCussTypeId_Second FASSTACTTYPEID
 from T_AR_VerificationBill t1
 inner join t_bd_customer tCus on tCus.FId = t1.FAcctCussID_Second
 where 1= 1 and ((t1.FVerificationType = 104 or t1.FVerificationType = 110 or t1.FVerificationType = 113 or t1.FVerificationType = 114) and ((t1.FIsSameCode = 1 AND t1.FIsSameCurrency = 0) or t1.FIsSameCode = 0))
 and t1.FCOMPANYID = ''sREAAAAAB9LM567U''
 and t1.FBizDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'')  and t1.FBizDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 union all
select A.FCompanyId FCompanyId, B.FAsstActId FSUPPLIERID, A.FCurrencyId FCURRENCYID, A.FBillDate FBILLDATE,
  0 FPayment, A.FAmount FActual , A.FAuditorID FAUDITORID, B.FAsstActTypeId FASSTACTTYPEID
 from T_AP_OTHERBILL A INNER JOIN T_AP_OTHERBILL B ON A.FSourceBillID = B.FID and A.FCompanyId = B.FCompanyId
 inner join t_bd_customer tCus on tCus.FId = B.FAsstActId
 where 1=1  and A.FCompanyId = ''sREAAAAAB9LM567U''

 and A.FBillDate >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'') and A.FBillDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 and A.FCurrencyId = B.FCurrencyId  and B.FCompanyId = ''sREAAAAAB9LM567U'' and B.FBillDate < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
  and B.FBillStatus = 3 and (A.FIsReverseBill = 0  and A.FIsTransBill = 1 and A.FBillStatus = 3 )
 union all
select A.FCompanyId FCompanyId, B.FPayeeID FSUPPLIERID, A.FCurrencyId FCURRENCYID,  A.FBizDate  FBILLDATE,
 A.FAmount FPayment, 0 FActual , A.FAuditorID FAUDITORID, B.FPayeeTypeID FASSTACTTYPEID
 from T_CAS_PaymentBill  A INNER JOIN T_CAS_PaymentBill  B ON A.FSourceBillID = B.FID and A.FCompanyId = B.FCompanyId
 inner join t_bd_customer tCus on tCus.FId = B.FPayeeID
 where 1=1  and A.FCompanyId = ''sREAAAAAB9LM567U''

 and  A.FBizDate  >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'') and  A.FBizDate  < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 and A.FCurrencyId = B.FCurrencyId  and B.FCompanyId = ''sREAAAAAB9LM567U'' and  B.FBizDate  < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
  and B.FSourceType= 101 and B.FBillStatus>= 15  AND A.FSourceType= 101 and A.FBillStatus>= 15 and A.FIsTransBill = 1
 union all
select A.FCompanyId FCompanyId, B.FAsstActID FSUPPLIERID, A.FCurrencyId FCURRENCYID,  A.FBizDate  FBILLDATE,
 0 AS FPayment, A.FAmount As FActual , A.FAuditorID FAUDITORID, B.FAsstActTypeID FASSTACTTYPEID
 from T_CAS_ReceivingBill A INNER JOIN T_AP_OtherBill B ON A.FSourceBillID = B.FID and A.FCompanyId = B.FCompanyId
 inner join t_bd_customer tCus on tCus.FId = B.FAsstActID
 where 1=1  and A.FCompanyId = ''sREAAAAAB9LM567U''

 and  A.FBizDate  >= to_date(''' || startdate ||  ''',''yyyy-mm-dd'') and  A.FBizDate  < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
 and A.FCurrencyId = B.FCurrencyId  and B.FCompanyId = ''sREAAAAAB9LM567U'' and  B.FBillDate  < to_date(''' || enddate ||  ''',''yyyy-mm-dd'')
  and A.FSourceType= 100 and A.FBillStatus>= 14 AND B.FBillStatus= 3 and A.FIsTransOtherBill = 1
 )  t1 left join t_bd_AsstActType t2 on t2.FId = t1.FAsstActTypeId
 left join t_bd_customer t3 on t3.FId = t1.FSupplierId left join t_bd_currency t4 on t4.FId = t1.FCurrencyId
 group by   t2.FId, t2.FName_l2 , t3.FNumber, t3.FName_l2 , t3.FId, t4.FName_l2 , t4.FId

)
where fcussnum = '''|| customerid || '''
group by fcussnum,fcussname ' ;

OPEN p_OutCursor FOR v_sql;

     test := v_sql;

      EXCEPTION
    WHEN OTHERS THEN
      DBMS_output.PUT_LINE(SQLERRM);
      test:=SQLERRM;

end YG501_GetCustomerBalance;

end;
