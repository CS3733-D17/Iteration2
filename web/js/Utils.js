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

function getMessage()
{
    var out = getCookie("SSINCSuc");
    out = out.replace('"','').replace('"','');
    if (document.getElementById("errOut")!=null)
    {
        document.getElementById("errOut").innerHTML = "<strong>"+out+"</strong>";
        if (out!=null && out.length>3)
            document.getElementById("errOut").style.display="block";
        else
            document.getElementById("errOut").style.display="none";
    }
}

function __fillForm_SUB()
{
    var out = JSON.parse(atob(getCookie("SSINCAP_GEN")));
    Object.keys(out).forEach(function(k){
        if (k=="source" || k=="type")
        {
            if (document.getElementById(out[k])!=null)
                document.getElementById(out[k]).checked = true;
        }
        else
        {
            if (document.getElementById(k)!=null)
            {
                document.getElementById(k).value = out[k];
            }
        }
    });
    out = JSON.parse(atob(getCookie("SSINCAP_DATA")));
    Object.keys(out).forEach(function(k){
        console.log("Set "+k+ " to "+out[k]);
        if (k=="source" || k=="type")
        {
            if (document.getElementById(out[k])!=null)
                document.getElementById(out[k]).checked = true;
        }
        else
        {
            if (document.getElementById(k)!=null)
            {
                document.getElementById(k).value = out[k];
            }
        }
    });
    out = JSON.parse(atob(getCookie("SSINCAP_LBL")));
    Object.keys(out).forEach(function(k){
        console.log("Set "+k+ " to "+out[k]);
        if (k=="source" || k=="type")
        {
            if (document.getElementById(out[k])!=null)
                document.getElementById(out[k]).checked = true;
        }
        else
        {
            if (document.getElementById(k)!=null)
            {
                document.getElementById(k).value = out[k];
            }
        }
    });
}

function fillForm()
{
    if(window.addEventListener){
        window.addEventListener('load', __fillForm_SUB)
    }else{
        window.attachEvent('onload', __fillForm_SUB)
    }
    
}


