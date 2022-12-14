package org.spring2.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;



import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.spring2.model.CouponTargetVO;
import org.spring2.model.CriteriaVO;
import org.spring2.model.DestinationVO;
import org.spring2.model.InquiryVO;
import org.spring2.model.MemberVO;
import org.spring2.model.PageVO;
import org.spring2.model.ReviewVO;
import org.spring2.model.UploadFileVO;
import org.spring2.service.CouponService;
import org.spring2.service.GetUserInfoService;
import org.spring2.service.MailSendService;
import org.spring2.service.MemberService;
import org.spring2.service.RestJsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.mysql.cj.xdevapi.JsonParser;

import net.coobird.thumbnailator.Thumbnailator;

@Controller
public class MemberController {
	@Autowired
	MemberService ms;

	@Autowired
	private MailSendService mailService;

	@Autowired
	CouponService cs;


	// ????????????
	@RequestMapping(value = "/member/signup", method = RequestMethod.GET)
	public String signup(HttpSession session) {
		session.invalidate();
		return "member/signup";
	}

	@RequestMapping(value = "/member/signup", method = RequestMethod.POST)
	public String signupin(MemberVO member) {
		try {
			member.setPhone(member.getPhone().substring(1, member.getPhone().length()));
			ms.signUp(member);
			CouponTargetVO ctvo = new CouponTargetVO(member.getId());
			System.out.println(ctvo);
			cs.signUpCoupon(ctvo);
			ms.point(member.getId());
			return "member/login";
		} catch (Exception e) {
			e.printStackTrace();
			return "member/signup";
		}
	}

	// ????????? ??????
	@RequestMapping(value = "/member/emailchk/{email}/", method = RequestMethod.GET)
	@ResponseBody
	public String mailCheck(@PathVariable String email) {
		System.out.println("????????? ?????? ????????? ?????????!");
		System.out.println("????????? ?????? ????????? : " + email);
		return mailService.joinEmail(email);

	}

	// ?????????, ?????????, ???????????? ????????????
	@RequestMapping(value = "/member/signup/{str}", method = RequestMethod.GET)
	public ResponseEntity<String> emchk(@PathVariable String str) {
		try {
			if (str.contains("@")) {
				System.out.println(str);
				return new ResponseEntity<>(ms.emchk(str).getEmail(), HttpStatus.OK);
			} else if (str.contains("!")) {
				str = str.substring(1, str.length());
				System.out.println(str);
				return new ResponseEntity<>(ms.phchk(str).getPhone(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(ms.idchk(str).getId(), HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ?????????
	@RequestMapping(value = "/member/login", method = RequestMethod.GET)
	public String login(HttpSession session,Model model) {
		session.invalidate();
		return "member/login";
	}

	@RequestMapping(value = "/member/login", method = RequestMethod.POST)
	public String loginPost(MemberVO member, HttpSession session) {
		System.out.println(ms.login(member));
		session.setAttribute("userInfo", ms.login(member));
		System.out.println(session.getAttribute("userInfo"));
		if (session.getAttribute("userInfo") != null) {

			return "redirect:/";
		} else {
			return "member/login";
		}
	}
	
// ????????? ?????????(js?????? js??? ???????????? ????????? ???????????? ????????? -> ???????????? ???????????? ????????? ?????? ?????? -> ????????? ?????? ???????????? ????????? ?????? ??????)
// 1???-login.js ????????????. RestJsonService.java , GetUserInfoService.java , pom.xml??? ?????????json??????(??????) ????????????
	@RequestMapping(value = "/snscheck", method = RequestMethod.GET)
	public String snsCheck(String code,HttpSession session,Model model) {
		
// 2. ?????? ??????????????? ???????????? ????????? ????????? ????????? ???????????? ?????????(???????????? ??????????????????.)
		RestJsonService restJsonService = new RestJsonService();

		//access_token??? ????????? JSON String??? ????????????.
		String accessTokenJsonData = restJsonService.getAccessTokenJsonData(code);
		System.out.println(accessTokenJsonData);
		if(accessTokenJsonData=="error") return "error";

		//JSON String -> JSON Object

		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(accessTokenJsonData);
			JSONObject jsonObj = (JSONObject) obj;
			System.out.println(jsonObj.get("access_token"));
			String accessToken = (String) jsonObj.get("access_token");
			session.setAttribute("accessToken",accessToken);
			
// 3. ????????? ????????? ????????? ????????? ????????? ????????? ???????????? ?????????(???????????? ??????????????????.)
			GetUserInfoService getUserInfoService = new GetUserInfoService();
			//?????? ????????? ????????? JSON String??? ????????????.
			String userInfo = getUserInfoService.getUserInfo(accessToken);
			System.out.println(userInfo);
			//JSON String -> JSON Object
			JSONParser userInfoParser = new JSONParser();
			Object uobj = userInfoParser.parse(userInfo);
			JSONObject userInfojsonObj = (JSONObject) uobj;
			System.out.println(userInfojsonObj.get("id"));

// 4. ????????? ?????? ??????
			JSONObject kakaoAccountJsonObject = (JSONObject)userInfojsonObj.get("kakao_account");
			JSONObject propertiesJsonObject = (JSONObject)userInfojsonObj.get("properties");
			String email = kakaoAccountJsonObject.get("email").toString();
			String id = userInfojsonObj.get("id").toString();
			String nickname = propertiesJsonObject.get("nickname").toString();
			String birthday = kakaoAccountJsonObject.get("birthday").toString();
			String month = birthday.substring(0,2);
			String day = birthday.substring(2);

			if(month.indexOf("0")==0) {
				month=month.substring(1);
			}
			if(day.indexOf("0")==0) {
				day=day.substring(1);
			}
// 5. MemberVO??? ??????. ---- ???!
			MemberVO member = new MemberVO();
			member.setId(email);
			member.setEmail(email);
			member.setPassword(id);
			member.setName(nickname);
			member.setBirth_m(month);
			member.setBirth_d(day);

			System.out.println(member);
			MemberVO mvo = new MemberVO();
			mvo= ms.snsCheck(member.getId());
			try {
				ms.snsSignup(member);
					        	CouponTargetVO ctvo = new CouponTargetVO(member.getId());
								System.out.println(ctvo);
								cs.signUpCoupon(ctvo);
			}catch(Exception e) {

				e.printStackTrace();
			}
			session.setAttribute("userInfo",ms.login(member));

		} catch (ParseException e) {
			e.printStackTrace();
		}


		return "redirect:/";
	}




// ????????? ?????????(???????????? ????????? ????????????????????? js?????? ?????? ??????)
// 1???-login.js , 2???3???-naverlogin.jsp ????????????
// 4. login.js?????? ????????????????????? ??????. naverlogin.jsp??? js?????? ?????? ????????? ??????????????????.
	@RequestMapping(value="/member/naverlogin", method=RequestMethod.GET)
	public String callBack(MemberVO member, HttpSession session){
		
		return null;
	}
// 5. naverlogin.jsp??? input????????? post??? ????????? MemberVO??? ?????? ????????????. ---- ???!
	@RequestMapping(value="/member/naverlogin", method=RequestMethod.POST)
	public String callBackPost(MemberVO member, HttpSession session){
		System.out.println(member);
		MemberVO mvo = new MemberVO();
		mvo= ms.snsCheck(member.getId());
		try {
			ms.naverSignup(member);
			        	CouponTargetVO ctvo = new CouponTargetVO(member.getId());
						System.out.println(ctvo);
						cs.signUpCoupon(ctvo);
		}catch(Exception e) {
			
			e.printStackTrace();
		}
		member.setPassword(member.getEmail());
		session.setAttribute("userInfo",ms.login(member));
		return "redirect:/";
	}
	
// ?????? ?????????(js?????? ??????????????? ?????? ????????????????????? ?????? -> ???????????? ????????? ?????? ????????? ????????? ?????? ??????)
// login.js , pom.xml??? ?????? ???????????????(??????) ?????? ????????????
	@RequestMapping(value="/googlelogin", method= RequestMethod.POST)

	public String googleLogin(String idtoken, Model model, HttpSession session) throws GeneralSecurityException, IOException {
// 3. js?????? ????????? ?????? ????????? ???????????? ?????? ???????????? ????????? ????????????. ????????? ?????? ????????????
		System.out.println(idtoken);
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
			    // Specify the CLIENT_ID of the app that accesses the backend:
			    .setAudience(Collections.singletonList("757014794840-uob6irtj1rk48pr65ckeor3ichdktgpc.apps.googleusercontent.com"))
			    // Or, if multiple clients access the backend:
			    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
			    .build();

			// (Receive idTokenString by HTTPS POST)
// 4. ????????? ????????? ???????????? ????????? ?????? ??????
			GoogleIdToken idToken = verifier.verify(idtoken);
			if (idToken != null) {
			  Payload payload = idToken.getPayload();

			  // Print user identifier
			  String userId = payload.getSubject();
			  System.out.println("User ID: " + userId);

			  // Get profile information from payload
			  String email = payload.getEmail();
			  boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			  String name = (String) payload.get("name");
			  String pictureUrl = (String) payload.get("picture");
			  String locale = (String) payload.get("locale");
			  String familyName = (String) payload.get("family_name");
			  String givenName = (String) payload.get("given_name");
			  // Use or store profile information
			  // ...

			  
			  
// 5. MemberVO??? ?????? ---- ???!
			  MemberVO member = new MemberVO();
				member.setId(userId);
				member.setEmail(userId);
				member.setPassword(userId);
				member.setName(name);
				member.setUserImg(pictureUrl);
				member.setPhone(userId);
				member.setBirth_d("1");
				member.setBirth_m("1");
				member.setBirth_y("1000");
				System.out.println(member);
			  
				MemberVO mvo = new MemberVO();
				mvo= ms.snsCheck(member.getId());
				try {
					ms.googleSignup(member);
						        	CouponTargetVO ctvo = new CouponTargetVO(member.getId());
									System.out.println(ctvo);
									cs.signUpCoupon(ctvo);
				}catch(Exception e) {

					e.printStackTrace();
				}
				session.setAttribute("userInfo",ms.login(member));
				System.out.println(session.getAttribute("userInfo"));
				return "redirect:/";
			  
			  
			} else {
			  System.out.println("Invalid ID token.");
			  return "/member/login";
			}		
		//		return null;

	}//googleLogin


	// ????????????
	@RequestMapping(value = "/member/logout", method = RequestMethod.GET)
	public String logoutget(HttpSession session) {

		

		session.invalidate();
		return "redirect:/";
	}






	// ???????????????
	@RequestMapping(value = "/member/findId", method = RequestMethod.GET)
	public String findIdGet() {
		return "member/findId";
	}

	// ????????? ?????? ????????? ??????
	@RequestMapping(value = "/member/emailchkID", method = RequestMethod.POST)
	public ResponseEntity<String> emailchkID(@RequestBody MemberVO member, Model model) {
		System.out.println("???????????? ????????? ??????");


		return mailService.findIdEmail(ms.find(member));
	}

	@RequestMapping(value = "/member/findId", method = RequestMethod.POST)
	public String findIdPost(MemberVO member, Model model) {
		try {

			System.out.println("????????? ?????? ???????????????.");
			System.out.println(ms.find(member));
			model.addAttribute("id", ms.find(member).getId());
			return "member/showId";

		} catch (Exception e) {
			e.printStackTrace();
			return "member/findId";
		}
	}

	@RequestMapping(value = "/member/showId", method = RequestMethod.GET)
	public String showIdGet() {
		return "member/findId";
	}

	// ???????????? ??????
	@RequestMapping(value = "/member/findPw", method = RequestMethod.GET)
	public String findPwGet() {
		return "member/findPw";
	}


	// ???????????? ?????? ????????? ??????
	@RequestMapping(value = "/member/emailchkPW", method = RequestMethod.POST)
	public ResponseEntity<String> emailchkPW(@RequestBody MemberVO member, Model model) {
		System.out.println("???????????? ???????????? ??????");


		return mailService.findPwEmail(ms.find(member));
	}

	@RequestMapping(value = "/member/findPw", method = RequestMethod.POST)
	public String findPwPost(MemberVO member, Model model) {
		try {

			System.out.println("???????????? ?????? ???????????????.");
			System.out.println(ms.find(member));
			model.addAttribute("id", ms.find(member).getId());

			if (ms.find(member) != null) {

				return "member/modifyPassword";
			}
			return "member/findPw";

		} catch (Exception e) {
			e.printStackTrace();
			return "member/findPw";
		}
	}

	@RequestMapping(value = "/member/destination", method = RequestMethod.GET)
	public void destination() {

	}

	// ????????? ?????? ??????
	@RequestMapping(value = "/destination/select/{id}", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<DestinationVO>> destinationGet(@PathVariable String id) {
		System.out.println(id);
		return new ResponseEntity<>(ms.destinationGet(id), HttpStatus.OK);
	}

	@RequestMapping(value = "/destination/insert", method = RequestMethod.POST)
	public ResponseEntity<String> destinationPost(@RequestBody DestinationVO des) {
		int result = ms.destinationPost(des);

		return result == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(value = "/destination/update", method = RequestMethod.PUT)
	public ResponseEntity<String> destinationPut(@RequestBody DestinationVO des) {
		int result = ms.destinationPut(des);

		return result == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(value = "/destination/delete", method = RequestMethod.DELETE)
	public ResponseEntity<String> destinationDelete(@RequestBody int dno) {
		System.out.println(dno);
		int result = ms.destinationDelete(dno);

		return result == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// ????????????
	@RequestMapping(value = "/member/memberList", method = RequestMethod.GET)
	public String memberList(HttpSession session) {
		if (session.getAttribute("userInfo") == null) {
			return "redirect:/";
		} else {
			return "member/memberList";
		}
	}

	@RequestMapping(value = "/memberList/select", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<MemberVO>> memberListGet(MemberVO member) {
		System.out.println(member.getId());
		return new ResponseEntity<>(ms.memberListGet(member), HttpStatus.OK);
	}

	// ?????? ????????????
	@RequestMapping(value = "/member/memberDetail", method = RequestMethod.GET)
	public void memberDetail(String id, Model model, CriteriaVO cri) {
		cri.setSearch(id);
//		cri.setAmount(5);
		int total = ms.total(cri);
		int totalQna = ms.totalQna(cri);
		
		model.addAttribute("paging",new PageVO(cri,total));
		System.out.println("1="+total);
		model.addAttribute("pagingQna",new PageVO(cri,totalQna));
		System.out.println("1="+totalQna);
		model.addAttribute("detail", ms.memberDetail(id));
	}

	// ???????????? ????????????
	@RequestMapping(value = "/member/modifyPassword", method = RequestMethod.GET)
	public String modifyPasswordGet(MemberVO member) {
		if(ms.find(member) != null) {
			return "member/modifyPassword";
		}else {
			return "redirect:/";
		}

	}

	@RequestMapping(value = "/member/modifyPassword", method = RequestMethod.POST)
	public String modifyPasswordPost(MemberVO member) {

		int result = ms.modifyPw(member);
		System.out.println(result);
		return result == 1 ? "member/login" : "redirect:/member/modifyPassword";
	}

	// ???????????????
	@RequestMapping(value = "/member/mypage", method = RequestMethod.GET)
	public String mypage() {
		
		
		return "member/mypage";
		//		}

	}
	

	// ???????????? ?????? ??? ???????????? ??????
	@RequestMapping(value = "/member/modifyInfoPWCheck", method = RequestMethod.GET)
	public String modifyGet(HttpSession session) {
		if (session.getAttribute("userInfo") == null) {
			return "redirect:/member/login";
		} else {
			return "member/modifyInfoPWCheck";
		}
	}

	@RequestMapping(value = "/member/modifyInfoPWCheck", method = RequestMethod.POST)
	public String modifyPost(MemberVO member, HttpSession session) {
		System.out.println(ms.modify(member));
		if (ms.modify(member) != null) {
			return "redirect:/member/memInfo";
		} else {
			return "member/modifyInfoPWCheck";
		}

	}

	// ???????????? ??????
	@RequestMapping(value = "/member/memInfo", method = RequestMethod.GET)
	public String memInfoGet(HttpSession session) {
		if (session.getAttribute("userInfo") == null) {
			return "redirect:/member/login";
		} else {
			return "member/memInfo";
		}
	}

	private String getFolder() {

		// ???????????? ??????(Thu Aug 24 09:23:12 KST 2022)
		Date date = new Date();
		// Thu Aug 24 09:23:12 KST 2022 -> 2022-08-24
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// ??????????????? ??????????????? ??????
		String str = sdf.format(date); // 2022-08-24
		// 2022-08-24 -> 2022\08\24??? ??????
		return str.replace("-", "\\");
	}

	private boolean checkImageType(File file) {
		// probeContentType(????????????).?????? ?????? ????????? ?????? ???????????? ???????????? ?????????.
		String contentType;
		try {
			contentType = Files.probeContentType(file.toPath());
			System.out.println("ContentType=" + contentType);
			// ??????????????? image?????? true, ??? ????????? false
			return contentType.startsWith("image");

		} catch (Exception e) {
			System.out.println(e.getMessage()); // ?????? ???????????? ??????

		}
		return false;

	}

	// ????????? ?????? ??????
	@RequestMapping(value = "/uploadAjax", method = RequestMethod.GET)
	public void uploadAjax() {

	}

	@RequestMapping(value = "/member/uploadAjaxAction", method = RequestMethod.POST)
	public ResponseEntity<ArrayList<UploadFileVO>> uploadAjaxPost(MultipartFile[] uploadFile) {

		// AttachFileVO????????? ??????
		ArrayList<UploadFileVO> list = new ArrayList<>();

		// ?????? ??????
		String uploadFolder = "D:\\01-STUDY\\upload";
		// ?????? ????????? ????????? getFolder???????????? ?????? ???????????? ????????? ????????? ?????? ??????
		File uploadPath = new File(uploadFolder, getFolder());

		// ????????????(D:\\upload\\????????????)
		if (uploadPath.exists() == false) {// uploadPath??? ???????????? ?????????,
			uploadPath.mkdirs();
		}

		// for(?????????:?????????)

		for (MultipartFile multipartFile : uploadFile) {

			// AttachFileVO???????????? ????????? ????????? ??????????????? ????????????
			// ArrayList??? ??????
			UploadFileVO attachvo = new UploadFileVO();

			System.out.println(multipartFile.getOriginalFilename());
			System.out.println(multipartFile.getSize());

			// ?????? ?????????(multipartFile.getOriginalFilename());
			// UUID ??????(UUID_multipartFile.getOriginalFilename());
			UUID uuid = UUID.randomUUID();
			System.out.println("UUID=" + uuid.toString());

			// AttachFileVo??? uploadPath ????????? ??????()
			attachvo.setPath(getFolder());
			// AttachFileVo??? fileName ????????? ??????()
			attachvo.setFileName(multipartFile.getOriginalFilename());
			// AttachFileVo??? uuid ????????? ??????()
			attachvo.setUuid(uuid.toString());

			// ?????? ??????
			// ???????????????(D:\\upload\\????????????),?????? ??????????????????(UUID_hodori.jpg)
			File saveFile = new File(uploadPath, uuid.toString() + "_" + multipartFile.getOriginalFilename());

			// D:\\upload\\hodori.jpg??? ????????? ??????(transferTo)
			// multipartFile.transferTo(saveFile);

			try {// transferTo()???????????? ????????? ?????????
				multipartFile.transferTo(saveFile); // ????????? ???????????? ??????
				// ?????? ????????? ???????????? ?????? ????????? ???????????????,
				if (checkImageType(saveFile)) {

					// AttachFileVo??? image ????????? ??????()
					attachvo.setCheckI(true);

					// ?????? ??????
					FileOutputStream thumbnail = new FileOutputStream(
							new File(uploadPath, "s_" + uuid.toString() + "_" + multipartFile.getOriginalFilename()));
					// ?????????????????? ?????? ??????
					Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnail, 100, 100);

					thumbnail.close();
				} // checkImageType????????? ???

				// AttachFileVO??? ????????? ???????????? ????????? ??????(add)
				list.add(attachvo);

			} catch (Exception e) { // ????????? ????????????
				System.out.println(e.getMessage()); // ?????? ???????????? ??????
			}
		} // for??? ???
		return new ResponseEntity<>(list, HttpStatus.OK);
	} // uploadAjax end...

	@RequestMapping(value = "/member/display", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getFile(String fileName) {
		System.out.println(fileName);

		File file = new File("D:\\01-STUDY\\upload\\" + fileName);

		ResponseEntity<byte[]> result = null;

		HttpHeaders headers = new HttpHeaders();

		try {
			headers.add("Content-Type", Files.probeContentType(file.toPath()));
			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}// getFile????????? ???...

	@RequestMapping(value = "/member/modifyUImg", method = RequestMethod.PUT)
	public ResponseEntity<String> modifyimg(@RequestBody MemberVO member, HttpSession session) {
		System.out.println(member);
		int result = ms.modifyimg(member);

		session.setAttribute("userInfo", ms.modifyNewInfo(member));
		return result == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// ????????? ??????
	@RequestMapping(value = "/member/modifyEmail", method = RequestMethod.PUT)
	public ResponseEntity<String> modifyEmail(@RequestBody MemberVO member, HttpSession session) {
		System.out.println(member);

		int result = ms.modifyEmail(member);
		System.out.println(result);

		session.setAttribute("userInfo", ms.modifyNewInfo(member));
		return result == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// ???????????? ??????
	@RequestMapping(value = "/member/modifyPhone", method = RequestMethod.PUT)
	public ResponseEntity<String> modifyPhone(@RequestBody MemberVO member, HttpSession session) {
		System.out.println("?????? : " + member);

		int result = ms.modifyPhone(member);
		System.out.println(result);

		session.setAttribute("userInfo", ms.modifyNewInfo(member));
		return result == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// ???????????? ??????
	@RequestMapping(value = "/member/modifyPw", method = RequestMethod.PUT)
	public ResponseEntity<String> modifyPw(@RequestBody MemberVO member, HttpSession session) {
		System.out.println(member);

		int result = ms.modifyPw(member);
		System.out.println(result);

		session.invalidate();
		return result == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// ????????????
	@RequestMapping(value = "/member/modifyDelete", method = RequestMethod.DELETE)
	public ResponseEntity<String> modifyDelete(@RequestBody MemberVO member, HttpSession session) {
		System.out.println(member);

		int result = ms.modifyDelete(member);
		System.out.println(result);

		session.invalidate();
		return result == 1 ? new ResponseEntity<>("success", HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	
//	@RequestMapping(value = "/member/memberDetail", method = RequestMethod.GET)
//	public void memberDetail(String id, Model model) {
//		model.addAttribute("detail", ms.memberDetail(id));
//	}
	// ??????????????? ??? ??????
	@RequestMapping(value = "/member/mypageBoardList", method = RequestMethod.GET)
	public void BoardList(String id, Model model, CriteriaVO cri) {
		cri.setSearch(id);
//		cri.setAmount(5);
		int total = ms.total(cri);
		int totalQna = ms.totalQna(cri);
		
		model.addAttribute("paging",new PageVO(cri,total));
		System.out.println("1="+total);
		model.addAttribute("pagingQna",new PageVO(cri,totalQna));
		System.out.println("1="+totalQna);
	}
	
	@RequestMapping(value = "/mypagereviewlist", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<ReviewVO>> mypageReviewList(ReviewVO rvo, CriteriaVO cri) {
		cri.setSearch(rvo.getId());
//		cri.setAmount(5);
		int total = ms.total(cri);
		System.out.println("?????? cri="+cri);
		System.out.println("????????????="+total);
		PageVO p = new PageVO(cri,total);
		
		System.out.println("???????????????="+p);
		System.out.println("????????????"+ms.mypageReviewList(cri));
		return new ResponseEntity<>(ms.mypageReviewList(cri),HttpStatus.OK);
//		return new ResponseEntity<>(ms.mypageReviewList(rvo), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mypageqnalist", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<InquiryVO>> mypageQnaList(InquiryVO ivo, CriteriaVO cri) {
		cri.setSearch(ivo.getId());
//		cri.setAmount(5);
		int total = ms.totalQna(cri);
		System.out.println("qna cri="+cri);
		System.out.println("qna??????="+total);
		PageVO p = new PageVO(cri,total);
		System.out.println("qna?????????="+p);
		System.out.println("qna??????"+ms.mypageQnaList(cri));
		return new ResponseEntity<>(ms.mypageQnaList(cri),HttpStatus.OK);
//		return new ResponseEntity<>(ms.mypageQnaList(ivo), HttpStatus.OK);
	}
	
	// ??????????????? ????????????
	@RequestMapping(value = "/member/mypageCouponList", method = RequestMethod.GET)
	public void CouponList() {
		
	}
	
	@RequestMapping(value = "/mypagecouponlist", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<CouponTargetVO>> mypageCouponList(CouponTargetVO coupon) {
		
		return new ResponseEntity<>(ms.mypageCouponList(coupon), HttpStatus.OK);
	}

}


