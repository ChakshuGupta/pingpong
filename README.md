# PingPong

This is the released version of PingPong.
Please read the NDSS 2020 paper titled
"[Packet-Level Signatures for Smart Home Devices](https://www.ndss-symposium.org/ndss-paper/packet-level-signatures-for-smart-home-devices)"
and the [manual PingPong 1.0](PingPong_1.0_Documentation.pdf) for further information.
Article reference:
```bibtex
@inproceedings{trimananda_packet-level_2020,
	address = {San Diego, CA},
	title = {Packet-{Level} {Signatures} for {Smart} {Home} {Devices}},
	isbn = {978-1-891562-61-7},
	url = {https://www.ndss-symposium.org/ndss-paper/packet-level-signatures-for-smart-home-devices/},
	doi = {10.14722/ndss.2020.24097},
	abstract = {Smart home devices are vulnerable to passive inference attacks based on network trafﬁc, even in the presence of encryption. In this paper, we present PINGPONG, a tool that can automatically extract packet-level signatures for device events (e.g., light bulb turning ON/OFF) from network trafﬁc. We evaluated PINGPONG on popular smart home devices ranging from smart plugs and thermostats to cameras, voice-activated devices, and smart TVs. We were able to: (1) automatically extract previously unknown signatures that consist of simple sequences of packet lengths and directions; (2) use those signatures to detect the devices or speciﬁc events with an average recall of more than 97\%; (3) show that the signatures are unique among hundreds of millions of packets of real world network trafﬁc; (4) show that our methodology is also applicable to publicly available datasets; and (5) demonstrate its robustness in different settings: events triggered by local and remote smartphones, as well as by homeautomation systems.},
	language = {en},
	urldate = {2023-11-07},
	booktitle = {Proceedings 2020 {Network} and {Distributed} {System} {Security} {Symposium}},
	publisher = {Internet Society},
	author = {Trimananda, Rahmadi and Varmarken, Janus and Markopoulou, Athina and Demsky, Brian},
	year = {2020},
}
```

## Docker container

To ease usage of the software, a Docker container wrapping the necessary configuration is provided.
Credits to François De Keersmaeker ([@fdekeers](https://github.com/fdekeers)).

### Build

The container's image is describe in the [Dockerfile](Dockerfile).

To build the image from scratch:
```bash
docker build .
```

### Run

You must provide data to run the software on.
The dataset used for the article's publication was released at https://athinagroup.eng.uci.edu/projects/pingpong/data.
Place your data inside the [data](data/) folder.

A [Docker compose configuration](docker-compose.yaml) is provided to start the container.
Update the command executed at container startup,
then run it with:
```bash
docker compose up
```

The files generated by the software will be placed in the corresponding locations in the [data](data/) folder.
As they are generated by the container, their owner is the `root` user.
You might want to change their ownership and/or permissions with `chown` or `chmod` respectively.
