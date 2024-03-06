/*% if (feature.MWM_TA_SensorDataCollector) { %*/
<template>
  <v-container class="pt-0">
    <v-row align="center" justify="space-between" no-gutters>
      <span class="text-h6"
        >{{ $t("sensorDataCollector.segments") }} {{ eventData.start.slice(11, 16) }} -
        {{ eventData.end.slice(11, 16) }}</span
      >
    </v-row>
    <v-row align="center">
      <v-data-table :headers="headers" :items="segments" sort-by="id">
        <template v-slot:[`item.startTime`]="{ item }">
          <span>{{ timestampArrayToEventTime(item.startTime) }}</span>
        </template>
        <template v-slot:[`item.endTime`]="{ item }">
          <span>{{ timestampArrayToEventTime(item.endTime) }}</span>
        </template>
      </v-data-table>
    </v-row>
  </v-container>
</template>

<script>

export default {
  name: "SegmentList",
  props: ["eventData"],
  data() {
    return {
      segments: [],
    };
  },
  computed: {
    headers() {
      return [
        {
          text: this.$t('sensorDataCollector.segmentId'),
          align: "left",
          sortable: true,
          value: "id"
        },
        {
          text: this.$t('sensorDataCollector.startTime'),
          align: "left",
          sortable: true,
          value: "startTime"
        },
        {
          text: this.$t('sensorDataCollector.endTime'),
          align: "left",
          sortable: true,
          value: "endTime"
        },
        {
          text: this.$t('sensorDataCollector.tag'),
          align: "left",
          sortable: true,
          value: "tag.name"
        }
      ];
    }
  },
  beforeMount() {
    this.segments = this.eventData.segments;
  },
  methods: {
    timestampArrayToEventTime(timestampArray) {
      //Formatter of timestamp array sent from server to hh:mm format
      return (
        ("0" + timestampArray[3].toString()).slice(-2) +
        ":" +
        ("0" + timestampArray[4].toString()).slice(-2)
      );
    }
  }
};
</script>
/*% } %*/
