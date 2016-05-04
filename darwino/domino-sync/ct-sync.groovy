nsfName 'ct.nsf'
darwinoDbName('ct')

form('Person') {
	storeId 'Person'
	field 'PID'
	field 'PCreationDate', type:DATETIME
	field 'PDisplayName'
	field 'PChampion'
	field 'PTwitter'
	field 'PStackOverflow'
	field 'PLinkedIn'
	field 'POpenNTF'
	field 'PLotusRegistration'
	field 'PEMail'
	field 'PPictureType'
	field 'PPictureURL'
	field 'Mypics', type:RICHTEXT
	field 'MypicNames', flags:[MULTIPLE]
	field 'CurrentMypic'
}
form('Category') {
	storeId 'Category'
	field 'CID'
	field 'CDisplayName'
	field 'CDescription'
	field 'COrder', type:NUMBER
}
form('Request') {
	storeId 'Request'
	field 'RCreationDate', type:DATETIME
	field 'RSubject'
	field 'REMail'
	field 'RCategory'
	field 'RBody'
	field 'RState'
}
form('News') {
	storeId 'News'
	field 'NID'
	field 'NCreationDate', type:DATETIME
	field 'NPublicationDate', type:DATETIME
	field 'NModerationDate', type:DATETIME
	field 'NModerator'
	field 'NLastEditor'
	field 'NLastModified', type:DATETIME
	field 'NTitle'
	field 'NLink'
	field 'PID'
	field 'NAbstract'
	field 'NImageURL'
	field 'TID', flags:[MULTIPLE]
	field 'NState'
	field 'NSpotlight'
	field 'NSpotlightSentence'
	field 'NSpotlightPosition'
	field 'NSpotlightPicture', type:RICHTEXT
	field 'NTopStory'
	field 'NTopStoryCategoryOrTop'
	field 'NTopStoryPosition'
	field 'NClicksTotal', type:NUMBER
	field 'NClicksLastWeek', type:NUMBER
	field 'NClicks', type:RICHTEXT
	field 'BID'
	field 'NHistory'
}
form('Blog') {
	storeId 'Blog'
	field 'BID'
	field 'BCreationDate', type:DATETIME
	field 'PID'
	field 'BDisplayName'
	field 'BDescription'
	field 'BURL'
	field 'BFeed'
	field 'BFeedHasRedirection'
	field 'BLastErrorMessage'
	field 'BLastSuccess', type:DATETIME
	field 'BLastTry', type:DATETIME
}
form('Config') {
	storeId 'Config'
	field 'COCaptchaPublicKey'
	field 'COCaptchaPrivateKey'
	field 'COAnalytics'
	field 'COComments'
}
form('Type') {
	storeId 'Type'
	field 'TID'
	field 'TDisplayName'
	field 'TDescription'
	field 'TModerators', flags:[NAMES, MULTIPLE]
	field 'TOrder', type:NUMBER
	field 'CID'
	field 'THash'
}
