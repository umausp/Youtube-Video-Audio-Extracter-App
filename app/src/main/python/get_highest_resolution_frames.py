# import requests
#
# def get_frames_from_url(url):
#     try:
#         response = requests.get(url)
#         if response.status_code == 200:
#             print("Downloaded")
#             # Open the local file in binary write mode and write the content
#             video_bytes = response.content
#             return video_bytes
#         else:
#             print(f"Failed to download video. Status code: {response.status_code}")
#
#     except Exception as e:
#         print(e)
#         return null