package stream.flarebot.flarebot.analytics;

import org.json.JSONObject;
import stream.flarebot.flarebot.FlareBot;
import stream.flarebot.flarebot.FlareBotManager;

import java.util.IntSummaryStatistics;
import java.util.concurrent.TimeUnit;

/**
 * This data is for tracking how much activity is happening on the bot at one time.
 * <p>
 * Data that is sent:
 *   Amount of guilds we have
 *   Amount of guilds loaded - In our cache
 *   The number of songs queued on the bot
 */
public class ActivityAnalytics implements AnalyticSender {

    @Override
    public JSONObject processData() {
        JSONObject object = new JSONObject();

        IntSummaryStatistics statistics = FlareBot.getInstance().getGuilds().stream().mapToInt(guild ->
                FlareBot.getInstance().getMusicManager().getPlayer(guild.getId()).getPlaylist().size()).summaryStatistics();

        object.put("data", new JSONObject()
                .put("guilds", FlareBotManager.getInstance().getGuilds().size())
                .put("guilds_with_songs", statistics.getCount())
                .put("songs", statistics.getSum()));
        return object;
    }

    @Override
    public long dataDeliveryFrequency() {
        return TimeUnit.MINUTES.toMillis(5);
    }

    @Override
    public String endpoint() {
        return "/activity";
    }
}
