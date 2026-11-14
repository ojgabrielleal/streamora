settings.log.stdout.set(true)

%include "generated-sources.liq"
%include "generated-schedule.liq"

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
