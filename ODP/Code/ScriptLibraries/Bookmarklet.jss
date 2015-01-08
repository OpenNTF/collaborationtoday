// © Copyright Karsten Lehmann/Mindoo 2010	
// © Copyright IBM Corp. 2011
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at:  
//
// http://www.apache.org/licenses/LICENSE-2.0  
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
// implied. See the License for the specific language governing 
// permissions and limitations under the License.
//
// Author: Karsten Lehmann 09/18/10
// Author: Niklas Heidloff 01/12/11 
// Author: Niklas Heidloff 07/26/12

function buildAddUrlBookmarkletCode(privateMode) {
	var exCon=facesContext.getExternalContext();
	var exReq = exCon.getRequest();
	var protocol=exReq.isSecure() ? "https" : "http";
	var host=exReq.getServerName();
	var port=exReq.getServerPort();
	var dbpath=database.getFilePath().replace('\\', '/');
	var dbpathenc=dbpath.replace("/","%2F");
	var cmd="addurl";
	var privateFlag=privateMode==true ? "1" : "0";
	var code="javascript:(function(){var%20c%3D%22"+cmd+"%22%3B%0Avar%20p%3D%22"+privateFlag+"%22%3B%0Avar%20b%3D%22"+protocol+"%3A%2F%2F"+host+":"+port+"%22%3B%0Avar%20d%3Db%2B%22%2F"+dbpathenc+"%22%3B%0Avar%20u%3Dd%2B%22%2Fadd.xsp%3FOpen%22%3B%0Avar%20e%3DencodeURIComponent%3B%0Avar%20t%3Ddocument.title%3Fe(document.title)%3A''%3B%0Avar%20o%3Ddocument.location%3Fe(document.location)%3A''%3B%0Avar%20head%3Ddocument.getElementsByTagName(%22head%22)%3B%0Avar%20body%3Ddocument.getElementsByTagName(%22body%22)%3B%0Avar%20dlg%3Ddocument.getElementById(%22mDocDialog%22)%3B%0Aif%20(dlg)%0A%09dlg.parentNode.removeChild(dlg)%3B%0Adlg%3Ddocument.createElement(%22div%22)%3B%0Adlg.id%3D%22mDocDialog%22%3B%0Avar%20g%3Ddlg.style%3B%0Ag.backgroundColor%3D%22%23ffffff%22%3B%0Ag.width%3D%22100%25%22%3B%0Ag.height%3D%22100%25%22%3B%0Ag.borderStyle%3D%22solid%22%3B%0Ag.borderColor%3D%22%23000000%22%3B%0Ag.borderWidth%3D%221px%22%3B%0Ag.position%3D%22absolute%22%3B%0Ag.top%3D%220px%22%3B%0Ag.left%3D%220px%22%3B%0Ag.zIndex%3D%222147483638%22%3B%0Ag.visibility%3D%22visible%22%3B%0Avar%20ifr%3Ddocument.createElement(%22iframe%22)%3B%0Avar%20h%3Difr.style%3B%0Ah.width%3D%22100%25%22%3B%0Ah.height%3D%22100%25%22%3B%0Ah.borderStyle%3D%22none%22%3B%0Ah.display%3D%22%22%3B%0Ah.visibility%3D%22visible%22%3B%0Aifr.src%3Du%2B%22%26c%3D%22%2Bc%2B%22%26t%3D%22%2Bt%2B%22%26b%3D%22%2Bo%2B%22%26p%3D%22%2Bp%3B%0Adlg.appendChild(ifr)%3B%0Abody%5B0%5D.appendChild(dlg)%3B}());";
	return code;
}


