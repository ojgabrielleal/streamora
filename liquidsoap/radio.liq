settings.log.stdout.set(true)

media = playlist(mode="randomize", reload=60, "/liquidsoap/m3u")
radio = fallback(track_sensitive=false, [media, blank()])

output.icecast(
  %mp3,
  host="icecast",
  port=8000,
  password=environment.get("ICECAST_SOURCE_PASSWORD"),
  mount=environment.get("ICECAST_MOUNT"),
  name="Streamora",
  description="Streamora Auto DJ",
  radio
)
