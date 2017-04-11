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

function unclickBox(id)
{
    document.getElementById("wineInfo").style.display= "none";
    if (document.getElementById(id)!=null)
        document.getElementById(id).checked = false;
    if (document.getElementById(id+"-lbl")!=null)
        document.getElementById(id+"-lbl").className = "btn btn-default";
}

function clickBox(id)
{
    if (document.getElementById(id)!=null)
        document.getElementById(id).checked = true;
    if (document.getElementById(id+"-lbl")!=null)
        document.getElementById(id+"-lbl").className += " active";
    if (id=="WINE")
    {
        document.getElementById("wineInfo").style.display = "block";
    }
}

function __fillForm_SUB()
{
    var out = JSON.parse(atob(getCookie("SSINCAP_GEN")));
    console.log(out);
    unclickBox("BEER");
    unclickBox("WINE");
    unclickBox("DISTILLED");
    unclickBox("IMPORTED");
    unclickBox("DOMESTIC");
    Object.keys(out).forEach(function(k){
        console.log("Set "+k+ " to "+out[k]);
        if (k=="source" || k=="type")
        {
            clickBox(out[k]);
        }
        else if (k=="appStatus")
        {
            if (document.getElementById(k)!=null)
            {
                document.getElementById(k).textContent = out[k];
            }
        }
        else if (k=="NEW" || k=="DISTINCT" || k=="EXEMPT" || k=="RESUBMIT")
        {
            console.log("Check "+k);
            if (document.getElementById(k)!=null)
                document.getElementById(k).checked = true;
        }
        else
        {
            if (document.getElementById(k)!=null)
            {
                document.getElementById(k).value = out[k];
            }
            else
            {
                console.log("WAS NULL");
            }
        }
    });
    out = JSON.parse(atob(getCookie("SSINCAP_DATA")));
    Object.keys(out).forEach(function(k){
        console.log("Set "+k+ " to "+out[k]);
        if (k=="source" || k=="type")
        {
            clickBox(out[k]);
        }
        else if (k=="appStatus")
        {
            if (document.getElementById(k)!=null)
                document.getElementById(k).innerHtml = out[k];
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
            clickBox(out[k]);
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


