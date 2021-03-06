/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.diameter.stack.functional.sh.base;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpSet;
import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.NetworkReqListener;
import org.jdiameter.api.OverloadException;
import org.jdiameter.api.Request;
import org.jdiameter.api.RouteException;
import org.jdiameter.api.app.AppAnswerEvent;
import org.jdiameter.api.app.AppRequestEvent;
import org.jdiameter.api.app.AppSession;
import org.jdiameter.api.sh.ClientShSession;
import org.jdiameter.api.sh.events.ProfileUpdateAnswer;
import org.jdiameter.api.sh.events.ProfileUpdateRequest;
import org.jdiameter.api.sh.events.PushNotificationAnswer;
import org.jdiameter.api.sh.events.PushNotificationRequest;
import org.jdiameter.api.sh.events.SubscribeNotificationsAnswer;
import org.jdiameter.api.sh.events.SubscribeNotificationsRequest;
import org.jdiameter.api.sh.events.UserDataAnswer;
import org.jdiameter.api.sh.events.UserDataRequest;
import org.jdiameter.common.impl.app.sh.ProfileUpdateRequestImpl;
import org.jdiameter.common.impl.app.sh.PushNotificationAnswerImpl;
import org.jdiameter.common.impl.app.sh.SubscribeNotificationsRequestImpl;
import org.jdiameter.common.impl.app.sh.UserDataRequestImpl;
import org.mobicents.diameter.stack.functional.Utils;
import org.mobicents.diameter.stack.functional.sh.AbstractShClient;

/**
 * Base implementation of Client
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ClientSh extends AbstractShClient {

  protected boolean sentSubscribeNotifications;
  protected boolean sentProfileUpdate;
  protected boolean sentUserDataRequest;
  protected boolean sentPushNotification;
  protected boolean receiveSubscribeNotifications;
  protected boolean receiveProfileUpdate;
  protected boolean receiveUserData;
  protected boolean receivePushNotification;

  protected PushNotificationRequest request;

  /**
   *
   */
  public ClientSh() {
  }

  public void sendSubscribeNotifications() throws Exception {
    SubscribeNotificationsRequest request = new SubscribeNotificationsRequestImpl(super.clientShSession.getSessions().get(0)
        .createRequest(SubscribeNotificationsRequest.code, getApplicationId(), getServerRealmName()));

    AvpSet avpSet = request.getMessage().getAvps();
    // < Subscribe-Notifications-Request > ::= < Diameter Header: 308, REQ, PXY, 16777217 >
    // < Session-Id >
    // { Vendor-Specific-Application-Id }
    // { Auth-Session-State }
    avpSet.addAvp(Avp.AUTH_SESSION_STATE, 1);
    // { Origin-Host }
    avpSet.removeAvp(Avp.ORIGIN_HOST);
    avpSet.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);
    // { Origin-Realm }
    // [ Destination-Host ]
    // { Destination-Realm }
    // *[ Supported-Features ]
    // { User-Identity }
    AvpSet userIdentity = avpSet.addGroupedAvp(Avp.USER_IDENTITY, getApplicationId().getVendorId(), true, false);
    // User-Identity ::= <AVP header: 700 10415>
    // [Public-Identity]
    userIdentity.addAvp(Avp.PUBLIC_IDENTITY, "tralalalal user", getApplicationId().getVendorId(), true, false, false);
    // [ Wildcarded-PSI ]
    // [ Wildcarded-IMPU ]
    // *[ Service-Indication ]
    // [ Send-Data-Indication ]
    // [ Server-Name ]
    // { Subs-Req-Type }
    avpSet.addAvp(Avp.SUBS_REQ_TYPE, 0, getApplicationId().getVendorId(), true, false, true);
    // *{ Data-Reference }
    avpSet.addAvp(Avp.DATA_REFERENCE, 0, getApplicationId().getVendorId(), true, false, true);
    // *[ Identity-Set ]
    // [ Expiry-Time ]
    // *[ DSAI-Tag ]
    // *[ AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]

    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    super.clientShSession.sendSubscribeNotificationsRequest(request);
    this.sentSubscribeNotifications = true;
  }

  public void sendProfileUpdate() throws Exception {
    ProfileUpdateRequest request = new ProfileUpdateRequestImpl(super.clientShSession.getSessions().get(0)
        .createRequest(ProfileUpdateRequest.code, getApplicationId(), getServerRealmName()));

    AvpSet avpSet = request.getMessage().getAvps();
    // < Profile-Update-Request > ::= < Diameter Header: 307, REQ, PXY, 16777217 >
    // < Session-Id >
    // { Auth-Session-State }
    avpSet.addAvp(Avp.AUTH_SESSION_STATE, 1);
    // { Origin-Host }
    avpSet.removeAvp(Avp.ORIGIN_HOST);
    avpSet.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);
    // { Origin-Realm }
    // [ Destination-Host ]
    // { Destination-Realm }
    // *[ Supported-Features ]
    // { User-Identity }
    AvpSet userIdentity = avpSet.addGroupedAvp(Avp.USER_IDENTITY, getApplicationId().getVendorId(), true, false);
    // User-Identity ::= <AVP header: 700 10415>
    // [Public-Identity]
    userIdentity.addAvp(Avp.PUBLIC_IDENTITY, "tralalalal user", getApplicationId().getVendorId(), true, false, false);
    // [MSISDN]
    // *[AVP]

    // [ Wildcarded-PSI ]
    // [ Wildcarded-IMPU ]
    // { Data-Reference }
    avpSet.addAvp(Avp.DATA_REFERENCE, 0, getApplicationId().getVendorId(), true, false, true);
    // { User-Data }
    avpSet.addAvp(Avp.USER_DATA_SH, "<xml><morexml></morexml></xml>", getApplicationId().getVendorId(), true, false, false);

    // *[ AVP ]
    super.clientShSession.sendProfileUpdateRequest(request);
    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    this.sentProfileUpdate = true;
  }

  public void sendUserData() throws Exception {
    UserDataRequest request =
        new UserDataRequestImpl(super.clientShSession.getSessions().get(0).createRequest(UserDataRequest.code, getApplicationId(), getServerRealmName()));

    AvpSet avpSet = request.getMessage().getAvps();
    // < User-Data -Request> ::= < Diameter Header: 306, REQ, PXY, 16777217 >
    // < Session-Id >
    // { Auth-Session-State }
    avpSet.addAvp(Avp.AUTH_SESSION_STATE, 1);
    // { Origin-Host }
    avpSet.removeAvp(Avp.ORIGIN_HOST);
    avpSet.addAvp(Avp.ORIGIN_HOST, getClientURI(), true);
    // { Origin-Realm }
    // [ Destination-Host ]
    // { Destination-Realm }
    // *[ Supported-Features ]
    // { User-Identity }
    AvpSet userIdentity = avpSet.addGroupedAvp(Avp.USER_IDENTITY, getApplicationId().getVendorId(), true, false);
    // User-Identity ::= <AVP header: 700 10415>
    // [Public-Identity]
    userIdentity.addAvp(Avp.PUBLIC_IDENTITY, "tralalalal user", getApplicationId().getVendorId(), true, false, false);
    // [MSISDN]
    // *[AVP]

    // [ Wildcarded-PSI ]
    // [ Wildcarded-IMPU ]
    // [ Server-Name ]
    // *[ Service-Indication ]
    // *{ Data-Reference }
    avpSet.addAvp(Avp.DATA_REFERENCE, 0, getApplicationId().getVendorId(), true, false, true);
    // *[ Identity-Set ]
    // [ Requested-Domain ]
    // [ Current-Location ]
    // *[ DSAI-Tag ]
    // *[ AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]

    Utils.printMessage(log, super.stack.getDictionary(), request.getMessage(), true);
    super.clientShSession.sendUserDataRequest(request);
    this.sentUserDataRequest = true;
  }

  public void sendUserDataRequest() throws Exception {
    UserDataRequest udr = super.createUDR(super.clientShSession);
    super.clientShSession.sendUserDataRequest(udr);
    this.sentUserDataRequest = true;
    Utils.printMessage(log, super.stack.getDictionary(), udr.getMessage(), isSentUserData());
  }

  public void sendPushNotification() throws Exception {
    if (!this.receivePushNotification || this.request == null) {
      fail("Did not receive NOTIFICATION or answer already sent.", null);
      throw new Exception("Did not receive NOTIFICATION or answer already sent. Request: " + this.request);
    }
    PushNotificationAnswer answer = new PushNotificationAnswerImpl((Request) request.getMessage(), 2001);

    AvpSet reqSet = request.getMessage().getAvps();

    AvpSet set = answer.getMessage().getAvps();
    set.removeAvp(Avp.DESTINATION_HOST);
    set.removeAvp(Avp.DESTINATION_REALM);
    set.addAvp(reqSet.getAvp(Avp.CC_REQUEST_TYPE), reqSet.getAvp(Avp.CC_REQUEST_NUMBER), reqSet.getAvp(Avp.AUTH_APPLICATION_ID));

    request = null;
    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    // < Push-Notification-Answer > ::=< Diameter Header: 309, PXY, 16777217 >
    // < Session-Id >
    // { Vendor-Specific-Application-Id }
    if (set.getAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID) == null) {
      AvpSet vendorSpecificApplicationId = set.addGroupedAvp(Avp.VENDOR_SPECIFIC_APPLICATION_ID, 0, false, false);
      // 1* [ Vendor-Id ]
      vendorSpecificApplicationId.addAvp(Avp.VENDOR_ID, getApplicationId().getVendorId(), true);
      // 0*1{ Auth-Application-Id }
      vendorSpecificApplicationId.addAvp(Avp.AUTH_APPLICATION_ID, getApplicationId().getAuthAppId(), true);
    }
    // [ Result-Code ]
    // [ Experimental-Result ]
    // { Auth-Session-State }
    set.addAvp(Avp.AUTH_SESSION_STATE, 1);
    // { Origin-Host }
    // { Origin-Realm }
    // *[ Supported-Features ]
    // *[ AVP ]
    // *[ Failed-AVP ]
    // *[ Proxy-Info ]
    // *[ Route-Record ]

    Utils.printMessage(log, super.stack.getDictionary(), answer.getMessage(), true);
    super.clientShSession.sendPushNotificationAnswer(answer);
    this.sentPushNotification = true;
  }

  // ------------ event handlers;

  @Override
  public void doSubscribeNotificationsAnswerEvent(ClientShSession session, SubscribeNotificationsRequest request, SubscribeNotificationsAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    receiveSubscribeNotifications = true;
  }

  @Override
  public void doProfileUpdateAnswerEvent(ClientShSession session, ProfileUpdateRequest request, ProfileUpdateAnswer answer) throws InternalException,
  IllegalDiameterStateException, RouteException, OverloadException {
    receiveProfileUpdate = true;
  }

  @Override
  public void doPushNotificationRequestEvent(ClientShSession session, PushNotificationRequest request)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    receivePushNotification = true;
    this.request = request;
  }

  @Override
  public void doUserDataAnswerEvent(ClientShSession session, UserDataRequest request, UserDataAnswer answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    receiveUserData = true;
  }

  @Override
  public void doOtherEvent(AppSession session, AppRequestEvent request, AppAnswerEvent answer)
      throws InternalException, IllegalDiameterStateException, RouteException, OverloadException {
    fail("Received \"Other\" event, request[" + request + "], answer[" + answer + "], on session[" + session + "]", null);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.jdiameter.api.NetworkReqListener#processRequest(org.jdiameter.api.Request)
   */
  @Override
  public Answer processRequest(Request request) {
    int code = request.getCommandCode();
    if (code != PushNotificationRequest.code) {
      fail("Received Request with code not used by Sh!. Code[" + request.getCommandCode() + "]", null);
      return null;
    }
    if (super.clientShSession.getSessionId().equals(request.getSessionId())) {
      // do fail?
      fail("Received Request in base listener, not in app specific!" + code, null);
    }
    else {
      super.clientShSession.release();
      try {
        super.clientShSession = this.sessionFactory.getNewAppSession(request.getSessionId(), getApplicationId(), ClientShSession.class, (Object) null);
        ((NetworkReqListener) this.clientShSession).processRequest(request);
      }
      catch (Exception e) {
        e.printStackTrace();
        fail(null, e);
      }
    }
    return null;
  }

  @Override
  protected String getClientURI() {
    return clientURI;
  }

  public boolean isSentSubscribeNotifications() {
    return sentSubscribeNotifications;
  }

  public boolean isSentProfileUpdate() {
    return sentProfileUpdate;
  }

  public boolean isSentUserData() {
    return sentUserDataRequest;
  }

  public boolean isSentPushNotification() {
    return sentPushNotification;
  }

  public boolean isReceiveSubscribeNotifications() {
    return receiveSubscribeNotifications;
  }

  public boolean isReceiveProfileUpdate() {
    return receiveProfileUpdate;
  }

  public boolean isReceiveUserData() {
    return receiveUserData;
  }

  public boolean isReceivePushNotification() {
    return receivePushNotification;
  }

  //*********************************************************//
  //***************** UDR methods ***************************//
  //*********************************************************//

  @Override
  protected String getPublicIdentity() {
    // 3GPP TS 29.172 v15.1.0 section 6.3.15
    String pi = "sip:fer@be-connect.us";
    return pi;
  }

  @Override
  protected byte[] getMSISDN() {
    String msisdnString = "59899077937";
    byte[] msisdn = msisdnString.getBytes();
    return msisdn;
  }

  @Override
  protected String getWildcardedPublicIdentity() {
    // 3GPP TS 29.172 v15.1.0 section 6.3.19
    String wpi = "sip:*@be-connect.us";
    return wpi;
  }

  @Override
  protected String getWildcardedIMPU() {
    // 3GPP TS 29.172 v15.1.0 section 6.3.20
    String wimpu = "tel:+598*";
    return wimpu;
  }

  @Override
  protected String getServerName() {
    // 3GPP TS 29.172 v15.1.0 section 6.3.9
    String serverName = "mme732@o2.com";
    return serverName;
  }

  @Override
  protected byte[] getServiceIndication() {
    // 3GPP TS 29.172 v15.1.0 section 6.3.5
    String siString = "MMTEL-PSTN-ISDN-CS-BINARY";
    byte[] serviceIndication = siString.getBytes();
    return serviceIndication;
  }

  @Override
  protected int getDataReference() {
    /*
    3GPP TS 29.172 v15.1.0 section 6.3.4
      The Data-Reference AVP is of type Enumerated, and indicates the type of the requested user data in the operation UDR and SNR.
      Its exact values and meaning is defined in 3GPP TS 29.328 [1]. The following values are defined (more details are given in 3GPP TS 29.328:
        RepositoryData (0)
        IMSPublicIdentity (10)
        IMSUserState (11)
        S-CSCFName (12)
        InitialFilterCriteria (13)
        This value is used to request initial filter criteria relevant to the requesting AS
        LocationInformation (14)
        UserState (15)
        ChargingInformation (16)
        MSISDN (17)
        PSIActivation (18)
        DSAI (19)
        ServiceLevelTraceInfo (21)
        IPAddressSecureBindingInformation (22)
        ServicePriorityLevel (23)
        SMSRegistrationInfo (24)
        UEReachabilityForIP (25)
        TADSinformation (26)
        STN-SR (27)
        UE-SRVCC-Capability (28)
        ExtendedPriority (29)
        CSRN (30)
        ReferenceLocationInformation (31)
        IMSI (32)
        IMSPrivateUserIdentity (33)
     */
    int dataReference = 14;
    return dataReference;
  }

  @Override
  protected int getIdentitySet() {
    /*
    3GPP TS 29.172 v15.1.0 section 6.3.10
      The Identity-Set AVP is of type Enumerated and indicates the requested set of IMS Public Identities.  The following values are defined:
        ALL_IDENTITIES (0)
        REGISTERED_IDENTITIES (1)
        IMPLICIT_IDENTITIES (2)
        ALIAS_IDENTITIES (3)
     */
    int identitySet = 0;
    return identitySet;
  }

  @Override
  protected int getRequestedDomain() {
    /*
    3GPP TS 29.172 v15.1.0 section 6.3.7
      The Requested-Domain AVP is of type Enumerated, and indicates the access domain for which certain data (e.g. user state) are requested.
      The following values are defined:
        CS-Domain (0)
        The requested data apply to the CS domain.
        PS-Domain (1)
        The requested data apply to the PS domain.
     */
    int requestedDomain = 1;
    return requestedDomain;
  }

  @Override
  protected int getCurrentLocation() {
    /*
    3GPP TS 29.172 v15.1.0 section 6.3.8
      The Current-Location AVP is of type Enumerated, and indicates whether an active location retrieval has to be initiated or not:
        DoNotNeedInitiateActiveLocationRetrieval (0)
          The request indicates that the initiation of an active location retrieval is not required.
        InitiateActiveLocationRetrieval (1)
          It is requested that an active location retrieval is initiated.
     */
    int currentLocation = 0;
    return currentLocation;
  }

  @Override
  protected byte[] getDSAITag() {
    // 3GPP TS 29.172 v15.1.0 section 6.3.18
    // The DSAI-Tag AVP is of type OctetString.
    // This AVP contains the DSAI-Tag identifying the instance of the Dynamic Service Activation Information being accessed for the Public Identity.
    String dsaiTagString = "19";
    byte[] dsaiTag = dsaiTagString.getBytes();
    return dsaiTag;
  }

  @Override
  protected int getSessionPriority() {
    /*
    3GPP TS 29.172 v15.1.0 section 6.3.21
      The Session-Priority AVP is of type Enumerated and indicates to the HSS the session's priority.
      The following values are defined:
        PRIORITY-0 (0)
        PRIORITY-1 (1)
        PRIORITY-2 (2)
        PRIORITY-3 (3)
        PRIORITY-4 (4)

        PRIORITY-0 is the highest priority.
     */
    int sessionPriority = 2;
    return sessionPriority;
  }

  @Override
  protected String getUserName() {
    // Information Element IMSI Mapped to AVP User-Name
    String imsi = "748039876543210";
    return imsi;
  }

  @Override
  protected long getRequestedNodes() {
    /*
    3GPP TS 29.172 v15.1.0 section 6.3.7A
        The Requested-Nodes AVP is of type Unsigned32 and it shall contain a bit mask. The meaning of the bits shall be as defined in table 6.3.7A/1:
        Table 6.3.7A/1: Requested-Nodes
        Bit	Name	                Description
        0	MME	                    The requested data apply to the MME
        1	SGSN	                The requested data apply to the SGSN
        2	3GPP-AAA-SERVER-TWAN	The requested data apply to the 3GPP AAA Server for TWAN
        3	AMF	                    The requested data apply to the AMF (for 3GPP access)
     */
    long requestedNodes = 2L;
    return requestedNodes;
  }

  @Override
  protected int getServingNodeIndication() {
    /*
    3GPP TS 29.172 v15.1.0 section 6.3.23
      The Serving-Node-Indication AVP is of type Enumerated.
      If present it indicates that the sender does not require any location information other than the Serving Node Addresses/Identities requested
      (e.g. MME name, VLR number). Other location information (e.g. Global Cell ID, Tracking Area ID) may be absent.
      The following values are defined:
      ONLY_SERVING_NODES_REQUIRED (0)
     */
    int servingNodeIndication = 2;
    return servingNodeIndication;
  }

  @Override
  protected int getPrePagingSupported() {
    /*
    3GPP TS 29.172 v15.1.0 section 6.3.26
    The Pre-paging-Supported AVP is of type Enumerated. It indicates whether the sender supports pre-paging or not. The following values are defined:
      PREPAGING_NOT_SUPPORTED (0)
      PREPAGING_SUPPORTED (1)
     */
    int prePagingSupported = 1;
    return prePagingSupported;
  }

  @Override
  protected int getLocalTimeZoneIndication() {
    /*
    3GPP TS 29.172 v15.1.0 section 6.3.27
        The Local-Time-Zone-Indication AVP is of type Enumerated. If present it indicates that the Local Time Zone information (time zone and daylight saving time) of the visited network where the UE is attached is requested with or without other location information. The following values are defined:
          ONLY_LOCAL_TIME_ZONE_REQUESTED (0)
          LOCAL_TIME_ZONE_WITH_LOCATION_INFO_REQUESTED (1)
     */
    int localTimeZoneIndication = 0;
    return localTimeZoneIndication;
  }

  @Override
  protected long getUDRFlags() {
    /*
    3GPP TS 29.172 v15.1.0 section 6.3.28
        The UDR-Flags AVP is of type Unsigned32 and it shall contain a bit mask. The meaning of the bits shall be as defined in 3GPP TS 29.328 [1].
          Table 6.3.28/1: UDR-Flags
          Bit	Name
          0	Location-Information-EPS-Supported
          1	RAT-Type-Requested
      NOTE:	Bits not defined in this table shall be cleared by the sender of the request and discarded by the receiver of the request.
     */
    long udrFlags = 0L;
    return udrFlags;
  }

  @Override
  protected byte[] getCallReferenceNumber() {
  /*
    3GPP TS 29.172 v15.1.0 section 6.3.30
      The Call-Reference-Number AVP is of type OctetString. The exact content and format of this AVP is described in 3GPP TS 29.002.
    3GPP TS 29.002 v15.0.0 section 7.6.5.1
      Call reference number
        This parameter refers to a call reference number allocated by a call control MSC.

    CallReferenceNumber ::= OCTET STRING (SIZE (1..8))
  */
    String callReferenceNumberString = "4143";
    byte[] callReferenceNumber = callReferenceNumberString.getBytes();
    return callReferenceNumber;
  }

  @Override
  protected byte[] getAsNumber() {
  /*
    3GPP TS 29.172 v15.1.0 section 6.3.31
      The AS-Number AVP is of type OctetString. The exact content and format of this AVP corresponds to the gmsc-address parameter described in 3GPP TS 29.002.
  */
    String asNumberString = "49";
    byte[] asNumber = asNumberString.getBytes();
    return asNumber;
  }

  @Override
  protected long getOcFeatureVector() {
    long ocFeatureVector = 2L;
    return ocFeatureVector;
  }

}
