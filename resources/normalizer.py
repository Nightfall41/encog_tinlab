import pandas as pd

df=pd.read_csv("all.csv", dtype={"SPEED":float64,"TRACK_POSITION":float64,"ANGLE_TO_TRACK_AXIS":float64,"TRACK_EDGE_0":float64,"TRACK_EDGE_1":float64,"TRACK_EDGE_2":float64,"TRACK_EDGE_3":float64,"TRACK_EDGE_4":float64,"TRACK_EDGE_5":float64,"TRACK_EDGE_6":float64,"TRACK_EDGE_7":float64,"TRACK_EDGE_8":float64,"TRACK_EDGE_9":float64,"TRACK_EDGE_10":float64,"TRACK_EDGE_11":float64,"TRACK_EDGE_12":float64,"TRACK_EDGE_13":float64,"TRACK_EDGE_14":float64,"TRACK_EDGE_15":float64,"TRACK_EDGE_16":float64,"TRACK_EDGE_17":float64,"TRACK_EDGE_18":float64,"ACCELERATION":float64,"BRAKE":float64,"STEERING":float64
})
df=df.dropna()

print(df)


output_csv = df.to_csv("all_cleaner.csv",index = None,header=True,float_format=".")