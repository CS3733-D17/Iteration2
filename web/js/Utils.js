function getCookie(name) {
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length == 2) return parts.pop().split(";").shift();
  else return "";
}

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
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
    if (document.getElementById("wineInfo")!=null)
        document.getElementById("wineInfo").style.display= "none";
    if (document.getElementById(id)!=null)
    {
        document.getElementById(id).checked = false;
    }
    if (document.getElementById(id+"-old")!=null)
    {
        document.getElementById(id+"-old").checked = false;
    }
    if (document.getElementById(id+"-lbl")!=null)
    {
        document.getElementById(id+"-lbl").className = "btn btn-default";
    }
}

function clickBox(id)
{
    if (document.getElementById("wineInfo")!=null)
        document.getElementById("wineInfo").style.display= "none";
    if (document.getElementById(id)!=null)
        document.getElementById(id).checked = true;
    if (document.getElementById(id+"-old")!=null)
        document.getElementById(id+"-old").checked = true;
    if (document.getElementById(id+"-lbl")!=null)
        document.getElementById(id+"-lbl").className += " active";
    if (id=="WINE")
    {
        if (document.getElementById("wineInfo")!=null)
            document.getElementById("wineInfo").style.display = "block";
    }
}

function __fillForm_SUB()
{
    if (document.getElementById("hideUpload")!=null)
    {
        if (document.getElementById("imgSelector")!=null)
        {
            document.getElementById("imgSelector").style.display = "none";
        }
        if (document.getElementById("imgSelector-old")!=null)
        {
            document.getElementById("imgSelector-old").style.display = "none";
        }
    }
    unclickBox("BEER");
    unclickBox("WINE");
    unclickBox("DISTILLED");
    unclickBox("IMPORTED");
    unclickBox("DOMESTIC");
    var out = JSON.parse(atob(getCookie("SSINCAP_GEN")));
    Object.keys(out).forEach(function(k){
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
            if (document.getElementById(k)!=null)
                document.getElementById(k).checked = true;
            if (document.getElementById(k+"-old")!=null)
                document.getElementById(k+"-old").checked = true;
        }
        else
        {
            if (document.getElementById(k)!=null)
            {
                document.getElementById(k).value = out[k];
            }
            if (document.getElementById(k+"-new")!=null)
            {
                document.getElementById(k+"-new").value = out[k];
            }
            if (document.getElementById(k+"-old")!=null)
            {
                document.getElementById(k+"-old").value = out[k];
            }
        }
    });
    if (document.getElementById("appSubmitNew")!=null)
    {
        if (document.getElementById("TBB_ID")!=null)
            document.getElementById("TBB_ID").value = "xxxxxxxxxxxx";
        if (document.getElementById("TBB_ID-old")!=null)
            document.getElementById("TBB_ID-old").value = "xxxxxxxxxxxx";
    }
    out = JSON.parse(atob(getCookie("SSINCAP_DATA")));
    Object.keys(out).forEach(function(k){
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
            if (document.getElementById(k+"-new")!=null)
            {
                document.getElementById(k+"-new").value = out[k];
            }
            if (document.getElementById(k+"-old")!=null)
            {
                document.getElementById(k+"-old").value = out[k];
            }
        }
    });
    out = JSON.parse(atob(getCookie("SSINCAP_LBL")));
    Object.keys(out).forEach(function(k){
        console.log(k +" -> "+out[k]);
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
            if (document.getElementById(k+"-new")!=null)
            {
                document.getElementById(k+"-new").value = out[k];
            }
            if (document.getElementById(k+"-old")!=null)
            {
                document.getElementById(k+"-old").value = out[k];
            }
        }
    });
    handleRevisionDisplays();
}

function fillForm()
{
    if(window.addEventListener){
        window.addEventListener('load', __fillForm_SUB)
    }else{
        window.attachEvent('onload', __fillForm_SUB)
    }
    
}

function handleRevOptDisp(className)
{
    var lst = document.getElementsByClassName(className);
    if (document.getElementById(className+"-div")!=null)
        document.getElementById(className+"-div").style.display = "none"; 
    var lst2 = document.getElementsByClassName(className+"-div-input");
    for (var i = 0; i<lst2.length; i++)
    {
        lst2[i].required = false;
    }
    for (var i = 0; i<lst.length; i++)
    {
        if (lst[i].checked)
        {
            if (document.getElementById(className+"-div")!=null)
                document.getElementById(className+"-div").style.display = "block";
            for (var i = 0; i<lst2.length; i++)
            {
                lst2[i].required = true;
            }
            return true;
        }
    }
    return false;
}

function handleRevisionDisplays()
{
    out = JSON.parse(atob(getCookie("SSINCAP_LBL")));
    var type = out["type"];
    var wineOpts = document.getElementsByClassName("tag-wine");
    for (var i = 0; i<wineOpts.length; i++)
    {
        wineOpts[i].style.display = "none";
    }
    var distilledOpts = document.getElementsByClassName("tag-distilled");
    for (var i = 0; i<distilledOpts.length; i++)
    {
        distilledOpts[i].style.display = "none";
    }
    var beerOpts = document.getElementsByClassName("tag-beer");
    for (var i = 0; i<beerOpts.length; i++)
    {
        beerOpts[i].style.display = "none";
    }
    if (type=="WINE")
    {
        for (var i = 0; i<wineOpts.length; i++)
        {
            wineOpts[i].style.display = "block";
        }
    }
    if (type=="BEER")
    {
        for (var i = 0; i<beerOpts.length; i++)
        {
            beerOpts[i].style.display = "block";
        }
    }
    if (type=="DISTILLED")
    {
        for (var i = 0; i<distilledOpts.length; i++)
        {
            distilledOpts[i].style.display = "block";
        }
    }
    var res = handleRevOptDisp("tag-alcohol");
    res |= handleRevOptDisp("tag-wine-vintage");
    res |= handleRevOptDisp("tag-wine-ph");
    res |= handleRevOptDisp("tag-wine-blend");
    res |= handleRevOptDisp("tag-formula");
    res |= handleRevOptDisp("tag-general");
    res |= handleRevOptDisp("tag-new-label");
    res |= handleRevOptDisp("tag-brandName");
    res |= handleRevOptDisp("tag-address");
    if (res)
    {
        if (document.getElementById("submitRevisions")!=null)
                document.getElementById("submitRevisions").style.display = "block";
    }
    else
    {
        if (document.getElementById("submitRevisions")!=null)
                document.getElementById("submitRevisions").style.display = "none";
    }
}

$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip(); 
});