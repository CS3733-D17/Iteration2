function getCookie(name) {
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length == 2) return parts.pop().split(";").shift();
  else return "";
}

function getEmail()
{
    console.log("Getting cookie\n");
    var out = getCookie("SSINCPN");
    console.log("Got" + out);
    return out;
}


