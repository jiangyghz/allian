function GetRequest() {
    let [url, theRequest] = [decodeURI(location.search), {}];
    if (url.indexOf('?') !== -1) {
        let str = url.substr(1);
        let strs = str.split('&');
        for (let i = 0; i < strs.length; i++) {
            theRequest[strs[i].split('=')[0]] = unescape(strs[i].split('=')[1])
        }
    }
    return theRequest
}

// const urlAfter = "http://skd.91jch.com/wxPage/data/";
const urlAfter = "./../../data/";
