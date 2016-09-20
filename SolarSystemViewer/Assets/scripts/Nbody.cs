using System;

public class Nbody
{
	public Nbody()
	{

	}

	public long id
	{
		get;
		set;
	}

	public int planetNumber
	{
		get;
		set;
	}

	public long step
	{
		get;
		set;
	}

	public double x
	{
		get;
		set;
	}

	public double y
	{
		get;
		set;
	}

	public double z
	{
		get;
		set;
	}

	public double u
	{
		get;
		set;
	}

	public double v
	{
		get;
		set;
	}

	public double w
	{
		get;
		set;
	}

	public override string ToString ()
	{
		return string.Format ("[Nbody: id={0}, planetNumber={1}, step={2}, x={3}, y={4}, z={5}, u={6}, v={7}, w={8}]", id, planetNumber, step, x, y, z, u, v, w);
	}

}