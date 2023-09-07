def compare(item1, item2):
    # if item1[1] == item2[1]:
    #     if item1[0]>=item2[0]:
    #         return 1
    #     else :
    #         return -1
    # else :
        if(item1[1]<=item2[1]):
            return -1
        else:
            return 1



import requests
def findCountry(region,keyword):
    url = f"https://jsonmock.hackerrank.com/api/countries/search?region={region}&name={keyword}"
    r = requests.get(url=url)
    json = r.json()
    n = json["total_pages"]
    
    list = []
    for i in range(0,n):
        url = f"https://jsonmock.hackerrank.com/api/countries/search?region={region}&name={keyword}&page={i}"
        r = requests.get(url=url)
        json = r.json()
        data = json["data"]
        for j in range(len(data)):
            cur = data[j]
            num = int(cur["population"])
            list.append([cur["name"],num])
    from functools import cmp_to_key
    sorted(list, key=cmp_to_key(compare))
    # list.sort(key=compare)
    newlist = []
    for s in list:
        x = s[0] +"," + str(s[1])
        newlist.append(x)
    return list
print(findCountry("Europe","e"))

