from bs4 import BeautifulSoup
import requests

url = "https://www.ttbonline.gov/colasonline/lookupProductClassTypeCode.do?action=search&display=all#search_results"

r  = requests.get(url)
data = r.text
soup = BeautifulSoup(data)

div = soup.find("div", {"class" : "box"})
i = 0
out = ""
for string in div.stripped_strings:
	i += 1
	if (i > 3):
		if ((i - 2) % 2 == 0):
			out = "ctList.add(new CT(\"" + (string) + "\", \""
		else:
			out = out + (string) + "\"));"
			print(out)

#print(div.get_text(strip=True))
