from pytube import YouTube
import json
import time


def load_video_from_url(youtube_url):
    try:
        yt = YouTube(youtube_url)
        streams = yt.streams


        current_epoch_time = int(time.time())

        video_audio_streams = yt.streams.filter(only_video=True)
        top_resolution_stream = [stream for stream in video_audio_streams if stream.includes_video_track and not stream.includes_audio_track]
        top_resolutions = top_resolution_stream[0]


        best_resolution_download_url = top_resolutions.url
        best_resolution = top_resolutions.resolution
        audio_stream = yt.streams.filter(only_audio=True).first()
        audio_url = audio_stream.url


        # Filter streams to select only video streams
        # video_streams = [stream for stream in streams if stream.mime_type.startswith('video/')]
        #
        # # Filter streams to select only streams that have both video and audio
        # video_audio_streams = [stream for stream in video_streams if stream.includes_video_track and stream.includes_audio_track]
        #
        # # Sort the video and audio streams by resolution in descending order
        # video_audio_streams.sort(key=lambda stream: stream.resolution, reverse=True)


        # Filter streams to select only streams that have both video and audio
        video_audio_streams = [stream for stream in streams if stream.includes_video_track and stream.includes_audio_track]

        # Sort the video and audio streams by resolution in descending order
        video_audio_streams.sort(key=lambda stream: int(stream.resolution[:-1]), reverse=True)

        # Select the top 2 video and audio streams
        best_2_video_audio_streams = video_audio_streams[:2]
        # https://img.youtube.com/vi/9Cp-hNvSWZs/maxresdefault.jpg
        # Replace with the actual YouTube video ID
        video_id = yt.video_id

        # Construct the maximum resolution thumbnail URL
        thumbnail_url = f"https://img.youtube.com/vi/{video_id}/sddefault.jpg"

        resolutions = {}
        response = []
    # Loop through the streams and extract resolution and URL
        for stream in best_2_video_audio_streams:
            if stream.resolution:
                my_map = {}
                download_url = stream.url
                thumbnail = thumbnail_url
                underscored_string = stream.title.replace(" ", "_")
                my_map["download_url"] = download_url
                my_map["name"] = stream.title
                my_map["thumb_nail"] = thumbnail
                my_map["resolution"] = stream.resolution
                my_map["title"] = f"{underscored_string}_{current_epoch_time}.mp4"
                my_map["best_resolution_video_only"] = best_resolution_download_url
                my_map["best_resolution"] = best_resolution
                my_map["audio_url"] = audio_url
                my_map["youtube_url"] = youtube_url
                response.append(my_map)

        # Print the resolution URLs
        # for resolution, url in resolutions.items():
        #     print(f'Resolution: {resolution}, URL: {url}')
        #
        #     urls.append(url)

        # my_map["download_url"] = urls

        # video_audio_streams = yt.streams.filter(only_video=True)
        # top_resolution_stream = [stream for stream in video_audio_streams if stream.includes_video_track and not stream.includes_audio_track]
        # top_resolutions = top_resolution_stream[0]
        #
        #
        # my_map = {}
        # download_url = top_resolutions.url
        # thumbnail = yt.thumbnail_url
        # underscored_string = top_resolutions.title.replace(" ", "_")
        # my_map["download_url"] = download_url
        # my_map["name"] = top_resolutions.title
        # my_map["thumb_nail"] = thumbnail
        # my_map["resolution"] = top_resolutions.resolution
        # my_map["title"] = f"{underscored_string}_{current_epoch_time}.mp4"
        #
        # print(my_map)
        # response.append(my_map)

        json_data = json.dumps(response, indent=4)
        print(json_data)

        return json_data
    except Exception as e:
        print(e)
        return ""